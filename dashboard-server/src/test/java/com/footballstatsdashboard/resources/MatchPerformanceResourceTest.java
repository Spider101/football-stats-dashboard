package com.footballstatsdashboard.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.footballstatsdashboard.api.model.ImmutableMatchPerformance;
import com.footballstatsdashboard.api.model.ImmutableUser;
import com.footballstatsdashboard.api.model.MatchPerformance;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.api.model.matchPerformance.ImmutableMatchRating;
import com.footballstatsdashboard.api.model.matchPerformance.MatchRating;
import com.footballstatsdashboard.db.MatchPerformanceDAO;
import com.footballstatsdashboard.db.key.ResourceKey;
import com.google.common.collect.ImmutableList;
import io.dropwizard.jackson.Jackson;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for match performance resource
 */
public class MatchPerformanceResourceTest {
    private static final String URI_PATH = "/match-performance";
    private static final ObjectMapper OBJECT_MAPPER = Jackson.newObjectMapper().copy();
    private static final String USER_EMAIL = "fake email";
    private User userPrincipal;

    private MatchPerformanceResource matchPerformanceResource;

    @Mock
    private MatchPerformanceDAO<ResourceKey> matchPerformanceDAO;

    @Mock
    private UriInfo uriInfo;

    @Before
    public void initialize() {
        MockitoAnnotations.openMocks(this);

        // TODO: 19/08/21 create base UT for all resources to encapsulat this common code
        UriBuilder uriBuilder = UriBuilder.fromPath(URI_PATH);
        when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);

        userPrincipal = ImmutableUser.builder()
                .email(USER_EMAIL)
                // other details are not required for the purposes of this test, so using empty strings
                .password("")
                .firstName("")
                .lastName("")
                .build();
        matchPerformanceResource = new MatchPerformanceResource(matchPerformanceDAO);
    }

    /**
     * given a valid match performance id, tests that the match performance data is successfully fetched from
     * couchbase server and returned in the response
     */
    @Test
    public void getMatchPerformance_fetchesMatchPerformanceDataFromCouchbase() {
        // setup
        UUID matchPerformanceId = UUID.randomUUID();
        MatchPerformance matchPerformanceFromCouchbase = getMatchPerformanceDataStub(matchPerformanceId, null, null, false);
        when(matchPerformanceDAO.getDocument(any(), any())).thenReturn(Pair.of(matchPerformanceFromCouchbase, 123L));

        // execute
        Response matchPerformanceResponse = matchPerformanceResource.getMatchPerformance(matchPerformanceId);

        // TODO: 19/08/21 refactor common assertions into an assertion helper
        // assert
        verify(matchPerformanceDAO).getDocument(any(), any());

        assertNotNull(matchPerformanceResponse);
        assertEquals(HttpStatus.OK_200, matchPerformanceResponse.getStatus());
        assertNotNull(matchPerformanceResponse.getEntity());

        MatchPerformance matchPerformanceFromResponse = OBJECT_MAPPER.convertValue(matchPerformanceResponse.getEntity(),
                MatchPerformance.class);
        assertEquals(matchPerformanceId, matchPerformanceFromResponse.getId());
    }

    /**
     * given a runtime exception is thrown by couchbase DAO when match performance data is not found, verifies
     * that the same exception is thrown by `getClub` resource method as well
     */
    @Test(expected = RuntimeException.class)
    public void getMatchPerformance_matchPerformanceNotFoundInCouchbase() {
        // setup
        UUID invalidMatchPerformanceId = UUID.randomUUID();
        when(matchPerformanceDAO.getDocument(any(), any()))
                .thenThrow(new RuntimeException("Unable to find document with ID: " + invalidMatchPerformanceId));

        // execute
        matchPerformanceResource.getMatchPerformance(invalidMatchPerformanceId);

        // assert
        verify(matchPerformanceDAO).getDocument(any(), any());
    }

    /**
     * given a valid match performance entity in the request, tests that the internal fields are set correctly on the
     * entity and persisted in couchbase
     */
    @Test
    public void createMatchPerformance_persistsMatchPerformanceInCouchbase() {
        // setup
        MatchPerformance incomingMatchPerformance = getMatchPerformanceDataStub(null, null, null, false);
        ArgumentCaptor<MatchPerformance> newMatchPerformanceCaptor = ArgumentCaptor.forClass(MatchPerformance.class);

        // execute
        Response matchPerformanceResponse = matchPerformanceResource.createMatchPerformance(userPrincipal,
                incomingMatchPerformance, uriInfo);

        // assert
        verify(matchPerformanceDAO).insertDocument(any(), newMatchPerformanceCaptor.capture());
        MatchPerformance capturedMatchPerformance = newMatchPerformanceCaptor.getValue();
        assertNotNull(capturedMatchPerformance);
        assertNotNull(capturedMatchPerformance.getCreatedDate());
        assertNotNull(capturedMatchPerformance.getLastModifiedDate());
        assertEquals(USER_EMAIL, capturedMatchPerformance.getCreatedBy());

        assertNotNull(matchPerformanceResponse);
        assertEquals(HttpStatus.CREATED_201, matchPerformanceResponse.getStatus());
        assertNotNull(matchPerformanceResponse.getEntity());

        // a matchPerformanceId is set on the matchPerformance instance created despite not setting one explicitly
        // due to the way the interface has been set up
        assertEquals(URI_PATH + "/" + incomingMatchPerformance.getId().toString(),
                matchPerformanceResponse.getLocation().getPath());
    }

    /**
     * given a valid match performance entity in the request, tests that an updated club entity with update internal
     * fields is upserted in couchbase
     */
    @Test
    public void updateMatchPerformance_updatesMatchPerformanceInCouchbase() {
        // setup
        UUID existingMatchPerformanceId = UUID.randomUUID();
        Long existingMatchPerformanceCAS = 123L;
        MatchPerformance existingMatchPerformanceInCouchbase = getMatchPerformanceDataStub(existingMatchPerformanceId,
                null, null, true);
        int updatedAppearances = existingMatchPerformanceInCouchbase.getAppearances() + 10;
        MatchPerformance incomingMatchPerformance = ImmutableMatchPerformance.builder()
                .from(getMatchPerformanceDataStub(existingMatchPerformanceId, null, null, false))
                .appearances(updatedAppearances)
                .build();

        ArgumentCaptor<ResourceKey> resourceKeyCaptor = ArgumentCaptor.forClass(ResourceKey.class);
        when(matchPerformanceDAO.getDocument(any(), any())).thenReturn(Pair.of(existingMatchPerformanceInCouchbase,
                existingMatchPerformanceCAS));

        ArgumentCaptor<MatchPerformance> matchPerformanceToBeUpdatedCaptor =
                ArgumentCaptor.forClass(MatchPerformance.class);

        // execute
        Response matchPerformanceResponse =
                matchPerformanceResource.updateMatchPerformance(existingMatchPerformanceId, incomingMatchPerformance);

        // assert
        verify(matchPerformanceDAO).getDocument(resourceKeyCaptor.capture(), any());
        ResourceKey capturedResourceKey = resourceKeyCaptor.getValue();
        assertEquals(existingMatchPerformanceId, capturedResourceKey.getResourceId());

        verify(matchPerformanceDAO).updateDocument(eq(capturedResourceKey), matchPerformanceToBeUpdatedCaptor.capture(),
                eq(existingMatchPerformanceCAS));
        MatchPerformance matchPerformanceToBeUpdated = matchPerformanceToBeUpdatedCaptor.getValue();
        assertNotNull(matchPerformanceToBeUpdated);
        assertEquals(userPrincipal.getEmail(), matchPerformanceToBeUpdated.getCreatedBy());
        assertEquals(LocalDate.now(), matchPerformanceToBeUpdated.getLastModifiedDate());

        assertNotNull(matchPerformanceResponse);
        assertEquals(HttpStatus.OK_200, matchPerformanceResponse.getStatus());
        assertNotNull(matchPerformanceResponse.getEntity());

        MatchPerformance matchPerformanceFromResponse = OBJECT_MAPPER.convertValue(matchPerformanceResponse.getEntity(),
                MatchPerformance.class);
        assertEquals(existingMatchPerformanceId, matchPerformanceFromResponse.getId());
        assertEquals(updatedAppearances, (int) matchPerformanceFromResponse.getAppearances());
    }

    /**
     * given that the request contains a match performance entity whose ID does not match the existing match
     * performance entity's ID, tests that the invalid entity is not upserted in couchbase and a server error
     * response is returned
     */
    @Test
    public void updateMatchPerformance_incomingMatchPerformanceIdDoesNotMatchExisting() {
        // setup
        UUID existingMatchPerformanceId = UUID.randomUUID();
        Long existingMatchPerformanceCAS = 123L;
        MatchPerformance existingMatchPerformance = getMatchPerformanceDataStub(existingMatchPerformanceId, null, null, true);
        when(matchPerformanceDAO.getDocument(any(), any())).thenReturn(Pair.of(existingMatchPerformance,
                existingMatchPerformanceCAS));

        UUID incorrectMatchPerformanceId = UUID.randomUUID();
        MatchPerformance incomingMatchPerformance = ImmutableMatchPerformance.builder()
                .from(existingMatchPerformance)
                .id(incorrectMatchPerformanceId)
                .build();

        // execute
        Response matchPerformanceResponse =
                matchPerformanceResource.updateMatchPerformance(existingMatchPerformanceId, incomingMatchPerformance);

        // assert
        verify(matchPerformanceDAO).getDocument(any(), any());
        verify(matchPerformanceDAO, never()).updateDocument(any(), any(), anyLong());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR_500, matchPerformanceResponse.getStatus());
        assertTrue(matchPerformanceResponse.getEntity().toString().contains(incorrectMatchPerformanceId.toString()));
    }

    /**
     * given a valid match performance ID, removes the match performance entity from couchbase
     */
    @Test
    public void deleteMatchPerformance_removesMatchPerformanceFromCouchbase() {
        // setup
        UUID matchPerformanceId = UUID.randomUUID();
        ArgumentCaptor<ResourceKey> resourceKeyCaptor = ArgumentCaptor.forClass(ResourceKey.class);

        // execute
        Response matchPerformanceResponse = matchPerformanceResource.deleteMatchPerformance(matchPerformanceId);

        // assert
        verify(matchPerformanceDAO).deleteDocument(resourceKeyCaptor.capture());
        ResourceKey capturedResourceKey = resourceKeyCaptor.getValue();
        assertNotNull(capturedResourceKey);
        assertEquals(matchPerformanceId, capturedResourceKey.getResourceId());

        assertNotNull(matchPerformanceResponse);
        assertEquals(HttpStatus.NO_CONTENT_204, matchPerformanceResponse.getStatus());
    }

    /**
     * given a valid player id and competition id, tests that the associated match performance entity is looked up in
     * couchbase server and returned in the response
     */
    @Test
    public void lookupMatchPerformanceByPlayerId_fetchesMatchPerformanceDataFromCouchbase() {
        // setup
        UUID playerId = UUID.randomUUID();
        UUID competitionId = UUID.randomUUID();
        UUID expectedMatchPerformanceId = UUID.randomUUID();
        MatchPerformance matchPerformanceFromCouchbase = getMatchPerformanceDataStub(expectedMatchPerformanceId,
                playerId, competitionId, false);
        when(matchPerformanceDAO.lookupMatchPerformanceByPlayerId(any(), any())).thenReturn(matchPerformanceFromCouchbase);

        // execute
        Response matchPerformanceResponse = matchPerformanceResource.lookupMatchPerformanceByPlayerId(playerId,
                competitionId);

        // assert
        verify(matchPerformanceDAO).lookupMatchPerformanceByPlayerId(eq(playerId), eq(competitionId));

        assertNotNull(matchPerformanceResponse);
        assertEquals(HttpStatus.OK_200, matchPerformanceResponse.getStatus());
        assertNotNull(matchPerformanceResponse.getEntity());

        MatchPerformance matchPerformanceFromResponse =
                OBJECT_MAPPER.convertValue(matchPerformanceResponse.getEntity(), MatchPerformance.class);
        assertEquals(playerId, matchPerformanceFromResponse.getPlayerId());
        assertEquals(competitionId, matchPerformanceFromResponse.getCompetitionId());
        assertEquals(expectedMatchPerformanceId, matchPerformanceFromResponse.getId());
    }

    @Test
    public void lookupMatchPerformanceByPlayerId_noMatchPerformanceDataFound() {
        // setup
        UUID playerId = UUID.randomUUID();
        UUID competitionId = UUID.randomUUID();
//        UUID expectedMatchPerformanceId = UUID.randomUUID();
//        MatchPerformance matchPerformanceFromCouchbase = getMatchPerformanceDataStub(expectedMatchPerformanceId,
//                playerId, competitionId, false);
        when(matchPerformanceDAO.lookupMatchPerformanceByPlayerId(any(), any())).thenReturn(null);

        // execute
        Response matchPerformanceResponse = matchPerformanceResource.lookupMatchPerformanceByPlayerId(playerId,
                competitionId);

        // assert
        verify(matchPerformanceDAO).lookupMatchPerformanceByPlayerId(eq(playerId), eq(competitionId));

        assertNotNull(matchPerformanceResponse);
        assertEquals(HttpStatus.NOT_FOUND_404, matchPerformanceResponse.getStatus());
//        assertNotNull(matchPerformanceResponse.getEntity());

//        MatchPerformance matchPerformanceFromResponse =
//                OBJECT_MAPPER.convertValue(matchPerformanceResponse.getEntity(), MatchPerformance.class);
//        assertEquals(playerId, matchPerformanceFromResponse.getPlayerId());
//        assertEquals(competitionId, matchPerformanceFromResponse.getCompetitionId());
//        assertEquals(expectedMatchPerformanceId, matchPerformanceFromResponse.getId());
    }

    private MatchPerformance getMatchPerformanceDataStub(UUID matchPerformanceId, UUID playerId, UUID competitionId,
                                                         boolean isExisting) {
        MatchRating matchRatingFromCouchbase = ImmutableMatchRating.builder()
                .current(7f)
                .history(ImmutableList.of(3.55f, 4f))
                .build();
        ImmutableMatchPerformance.Builder matchPerformanceBuilder = ImmutableMatchPerformance.builder()
                .playerId(playerId != null ? playerId : UUID.randomUUID())
                .competitionId(competitionId != null ? competitionId : UUID.randomUUID())
                .appearances(10)
                .goals(10)
                .dribbles(10)
                .passCompletionRate(80.0f)
                .assists(10)
                .yellowCards(10)
                .redCards(10)
                .tackles(10)
                .matchRating(matchRatingFromCouchbase)
                .playerOfTheMatch(10)
                .penalties(10)
                .fouls(10);

        if (matchPerformanceId != null) {
            matchPerformanceBuilder.id(matchPerformanceId);
        }

        if (isExisting) {
            matchPerformanceBuilder.createdBy(USER_EMAIL);

            Instant currentInstant = Instant.now();
            Instant olderInstant = currentInstant.minus(1, ChronoUnit.DAYS);
            matchPerformanceBuilder.lastModifiedDate(LocalDate.ofInstant(olderInstant, ZoneId.systemDefault()));
        }
        return matchPerformanceBuilder.build();
    }
}