package com.footballstatsdashboard.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.footballstatsdashboard.core.utils.FixtureLoader;
import com.footballstatsdashboard.api.model.ImmutableMatchPerformance;
import com.footballstatsdashboard.api.model.ImmutableUser;
import com.footballstatsdashboard.api.model.MatchPerformance;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.db.IMatchPerformanceEntityDAO;
import com.google.common.collect.ImmutableList;
import io.dropwizard.jackson.Jackson;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
    private static final int UPDATED_PLAYER_APPEARANCES = 20;
    private static final FixtureLoader FIXTURE_LOADER = new FixtureLoader(OBJECT_MAPPER);

    private User userPrincipal;
    private MatchPerformanceResource matchPerformanceResource;

    @Mock
    private IMatchPerformanceEntityDAO matchPerformanceDAO;

    @Mock
    private UriInfo uriInfo;

    @Before
    public void initialize() {
        MockitoAnnotations.openMocks(this);

        // TODO: 19/08/21 create base UT for all resources to encapsulate this common code
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
    public void getMatchPerformanceFetchesMatchPerformanceDataFromCouchbase() {
        // setup
        UUID matchPerformanceId = UUID.randomUUID();
        MatchPerformance matchPerformanceFromCouchbase = getMatchPerformanceDataStub(matchPerformanceId, null, null,
                false);
        when(matchPerformanceDAO.getEntity(eq(matchPerformanceId))).thenReturn(matchPerformanceFromCouchbase);

        // execute
        Response matchPerformanceResponse = matchPerformanceResource.getMatchPerformance(matchPerformanceId);

        // TODO: 19/08/21 refactor common assertions into an assertion helper
        // assert
        verify(matchPerformanceDAO).getEntity(any());

        assertNotNull(matchPerformanceResponse);
        assertEquals(HttpStatus.OK_200, matchPerformanceResponse.getStatus());
        assertNotNull(matchPerformanceResponse.getEntity());

        MatchPerformance matchPerformanceFromResponse = OBJECT_MAPPER.convertValue(matchPerformanceResponse.getEntity(),
                MatchPerformance.class);
        assertEquals(matchPerformanceId, matchPerformanceFromResponse.getId());
    }

    // TODO: 28/04/22 this should be throwing an entity not found exception
    /**
     * given a runtime exception is thrown by couchbase DAO when match performance data is not found, verifies
     * that the same exception is thrown by `getClub` resource method as well
     */
    @Test
    public void getMatchPerformanceWhenMatchPerformanceNotFoundInCouchbase() {
        // setup
        UUID invalidMatchPerformanceId = UUID.randomUUID();
        when(matchPerformanceDAO.getEntity(eq(invalidMatchPerformanceId)))
                .thenThrow(new RuntimeException("Unable to find document with ID: " + invalidMatchPerformanceId));

        // execute
        assertThrows(RuntimeException.class,
                () -> matchPerformanceResource.getMatchPerformance(invalidMatchPerformanceId));

        // assert
        verify(matchPerformanceDAO).getEntity(any());
    }

    /**
     * given a valid match performance entity in the request, tests that the internal fields are set correctly on the
     * entity and persisted in couchbase
     */
    @Test
    public void createMatchPerformancePersistsMatchPerformanceInCouchbase() {
        // setup
        MatchPerformance incomingMatchPerformance = getMatchPerformanceDataStub(null, null, null, false);
        ArgumentCaptor<MatchPerformance> newMatchPerformanceCaptor = ArgumentCaptor.forClass(MatchPerformance.class);

        // execute
        Response matchPerformanceResponse = matchPerformanceResource.createMatchPerformance(userPrincipal,
                incomingMatchPerformance, uriInfo);

        // assert
        verify(matchPerformanceDAO).insertEntity(newMatchPerformanceCaptor.capture());
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
    public void updateMatchPerformanceUpdatesMatchPerformanceInCouchbase() {
        // setup
        UUID existingMatchPerformanceId = UUID.randomUUID();
        MatchPerformance existingMatchPerformanceInCouchbase = getMatchPerformanceDataStub(existingMatchPerformanceId,
                null, null, true);
        MatchPerformance incomingMatchPerformance = ImmutableMatchPerformance.builder()
                .from(getMatchPerformanceDataStub(existingMatchPerformanceId, null, null, false))
                .appearances(UPDATED_PLAYER_APPEARANCES)
                .build();

        when(matchPerformanceDAO.getEntity(eq(existingMatchPerformanceId)))
                .thenReturn(existingMatchPerformanceInCouchbase);

        ArgumentCaptor<MatchPerformance> matchPerformanceToBeUpdatedCaptor =
                ArgumentCaptor.forClass(MatchPerformance.class);

        // execute
        Response matchPerformanceResponse =
                matchPerformanceResource.updateMatchPerformance(existingMatchPerformanceId, incomingMatchPerformance);

        // assert
        verify(matchPerformanceDAO).getEntity(any());
        verify(matchPerformanceDAO).updateEntity(eq(existingMatchPerformanceId),
                matchPerformanceToBeUpdatedCaptor.capture());
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
        assertEquals(UPDATED_PLAYER_APPEARANCES, (int) matchPerformanceFromResponse.getAppearances());
    }

    /**
     * given that the request contains a match performance entity whose ID does not match the existing match
     * performance entity's ID, tests that the invalid entity is not upserted in couchbase and a server error
     * response is returned
     */
    @Test
    public void updateMatchPerformanceWhenIncomingMatchPerformanceIdDoesNotMatchExisting() {
        // setup
        UUID existingMatchPerformanceId = UUID.randomUUID();
        MatchPerformance existingMatchPerformance = getMatchPerformanceDataStub(existingMatchPerformanceId, null, null,
                true);
        when(matchPerformanceDAO.getEntity(eq(existingMatchPerformanceId))).thenReturn(existingMatchPerformance);

        UUID incorrectMatchPerformanceId = UUID.randomUUID();
        MatchPerformance incomingMatchPerformance = ImmutableMatchPerformance.builder()
                .from(existingMatchPerformance)
                .id(incorrectMatchPerformanceId)
                .build();

        // execute
        Response matchPerformanceResponse =
                matchPerformanceResource.updateMatchPerformance(existingMatchPerformanceId, incomingMatchPerformance);

        // assert
        verify(matchPerformanceDAO).getEntity(any());
        verify(matchPerformanceDAO, never()).updateEntity(any(), any());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR_500, matchPerformanceResponse.getStatus());
        assertTrue(matchPerformanceResponse.getEntity().toString().contains(incorrectMatchPerformanceId.toString()));
    }

    /**
     * given a valid match performance ID, removes the match performance entity from couchbase
     */
    @Test
    public void deleteMatchPerformanceRemovesMatchPerformanceFromCouchbase() {
        // setup
        UUID matchPerformanceId = UUID.randomUUID();

        // execute
        Response matchPerformanceResponse = matchPerformanceResource.deleteMatchPerformance(matchPerformanceId);

        // assert
        verify(matchPerformanceDAO).deleteEntity(eq(matchPerformanceId));

        assertNotNull(matchPerformanceResponse);
        assertEquals(HttpStatus.NO_CONTENT_204, matchPerformanceResponse.getStatus());
    }

    /**
     * given a valid player id and competition id, tests that the associated match performance entity is looked up in
     * couchbase server and returned in the response
     */
    @Test
    public void lookupMatchPerformanceByPlayerIdFetchesMatchPerformanceDataFromCouchbase() {
        // setup
        UUID playerId = UUID.randomUUID();
        UUID competitionId = UUID.randomUUID();
        UUID expectedMatchPerformanceId = UUID.randomUUID();
        MatchPerformance matchPerformanceFromCouchbase = getMatchPerformanceDataStub(expectedMatchPerformanceId,
                playerId, competitionId, false);
        when(matchPerformanceDAO.getMatchPerformanceOfPlayerInCompetition(eq(playerId), eq(competitionId)))
                .thenReturn(ImmutableList.of(matchPerformanceFromCouchbase));

        // execute
        Response matchPerformanceResponse = matchPerformanceResource.lookupMatchPerformanceByPlayerId(playerId,
                competitionId);

        // assert
        verify(matchPerformanceDAO).getMatchPerformanceOfPlayerInCompetition(any(), any());

        assertNotNull(matchPerformanceResponse);
        assertEquals(HttpStatus.OK_200, matchPerformanceResponse.getStatus());
        assertNotNull(matchPerformanceResponse.getEntity());

        TypeReference<List<MatchPerformance>> matchPerformanceListTypeRef = new TypeReference<>() { };
        List<MatchPerformance> matchPerformancesFromResponse =
                OBJECT_MAPPER.convertValue(matchPerformanceResponse.getEntity(), matchPerformanceListTypeRef);
        assertFalse(matchPerformancesFromResponse.isEmpty());
        matchPerformancesFromResponse.forEach(matchPerformanceFromResponse -> {
            assertNotNull(matchPerformanceFromResponse);
            assertEquals(playerId, matchPerformanceFromResponse.getPlayerId());
            assertEquals(competitionId, matchPerformanceFromResponse.getCompetitionId());
            assertEquals(expectedMatchPerformanceId, matchPerformanceFromResponse.getId());
        });
    }

    @Test
    public void lookupMatchPerformanceByPlayerIdWhenNoMatchPerformanceDataFound() {
        // setup
        UUID playerId = UUID.randomUUID();
        UUID competitionId = UUID.randomUUID();
        when(matchPerformanceDAO.getMatchPerformanceOfPlayerInCompetition(eq(playerId), eq(competitionId)))
                .thenReturn(new ArrayList<>());

        // execute
        Response matchPerformanceResponse = matchPerformanceResource.lookupMatchPerformanceByPlayerId(playerId,
                competitionId);

        // assert
        verify(matchPerformanceDAO).getMatchPerformanceOfPlayerInCompetition(any(), any());

        assertNotNull(matchPerformanceResponse);
        assertEquals(HttpStatus.NOT_FOUND_404, matchPerformanceResponse.getStatus());
    }

    private MatchPerformance getMatchPerformanceDataStub(UUID matchPerformanceId, UUID playerId, UUID competitionId,
                                                        boolean isExisting) {
        MatchPerformance matchPerformanceFromFixture;
        try {
            matchPerformanceFromFixture = FIXTURE_LOADER.loadFixture("fixtures/match-performance.json",
                    MatchPerformance.class);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
        ImmutableMatchPerformance.Builder matchPerformanceBuilder = ImmutableMatchPerformance.builder()
                .from(matchPerformanceFromFixture)
                .playerId(playerId != null ? playerId : matchPerformanceFromFixture.getPlayerId())
                .competitionId(competitionId != null ? competitionId : matchPerformanceFromFixture.getCompetitionId());

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