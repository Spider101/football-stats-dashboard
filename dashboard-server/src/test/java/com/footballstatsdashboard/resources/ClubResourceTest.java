package com.footballstatsdashboard.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.footballstatsdashboard.api.model.club.Club;
import com.footballstatsdashboard.api.model.club.ImmutableClub;
import com.footballstatsdashboard.api.model.ImmutableUser;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.api.model.club.ImmutableSquadPlayer;
import com.footballstatsdashboard.api.model.club.SquadPlayer;
import com.footballstatsdashboard.db.ClubDAO;
import com.footballstatsdashboard.db.key.ResourceKey;
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
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for club resource
 */
public class ClubResourceTest {
    private static final String URI_PATH = "/club";
    private static final ObjectMapper OBJECT_MAPPER = Jackson.newObjectMapper().copy();
    public static final String USER_EMAIL = "fake email";
    private User userPrincipal;

    private ClubResource clubResource;

    @Mock
    private ClubDAO<ResourceKey> clubDAO;

    @Mock
    private UriInfo uriInfo;

    /**
     * set up test data before each test case is run
     */
    @Before
    public void initialize() {
        MockitoAnnotations.openMocks(this);

        UriBuilder uriBuilder = UriBuilder.fromPath(URI_PATH);
        when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);

        userPrincipal = ImmutableUser.builder()
                .email(USER_EMAIL)
                // other details are not required for the purposes of this test, so using empty strings
                .password("")
                .firstName("")
                .lastName("")
                .build();

        clubResource = new ClubResource(clubDAO);
    }

    /**
     * given a valid club id, tests that the club entity is successfully fetched from couchbase server and returned
     * in the response
     */
    @Test
    public void getClub_fetchesClubFromCouchbase() {
        // setup
        UUID clubId = UUID.randomUUID();
        Club clubFromCouchbase = getClubDataStub(clubId, false);
        when(clubDAO.getDocument(any(), any())).thenReturn(clubFromCouchbase);

        // execute
        Response clubResponse = clubResource.getClub(clubId);

        // assert
        verify(clubDAO).getDocument(any(), any());
        assertEquals(HttpStatus.OK_200, clubResponse.getStatus());
        assertNotNull(clubResponse.getEntity());

        Club clubFromResponse = OBJECT_MAPPER.convertValue(clubResponse.getEntity(), Club.class);
        assertEquals(clubId, clubFromResponse.getId());
    }

    /**
     * given a runtime exception is thrown by couchbase DAO when club entity is not found, verifies that the same
     * exception is thrown by `getClub` resource method as well
     */
    @Test(expected = RuntimeException.class)
    public void getClub_clubNotFoundInCouchbase() {
        // setup
        UUID invalidClubId = UUID.randomUUID();
        when(clubDAO.getDocument(any(), any()))
                .thenThrow(new RuntimeException("Unable to find document with ID: " + invalidClubId));

        // execute
        clubResource.getClub(invalidClubId);

        // assert
        verify(clubDAO).getDocument(any(), any());
    }

    /**
     * given a valid club entity in the request, tests that the internal fields are set correctly on the entity and
     * persisted in couchbase
     */
    @Test
    public void createClub_persistsClubInCouchbase() {
        // setup
        Club incomingClub = getClubDataStub(null, false);
        ArgumentCaptor<Club> newClubCaptor = ArgumentCaptor.forClass(Club.class);

        // execute
        Response clubResponse = clubResource.createClub(userPrincipal, incomingClub, uriInfo);

        // assert
        verify(clubDAO).insertDocument(any(), newClubCaptor.capture());
        Club newClub = newClubCaptor.getValue();
        assertNotNull(newClub.getCreatedDate());
        assertNotNull(newClub.getLastModifiedDate());
        assertEquals(userPrincipal.getEmail(), newClub.getCreatedBy());

        assertEquals(HttpStatus.CREATED_201, clubResponse.getStatus());
        assertNotNull(clubResponse.getEntity());

        // a clubId is set on the club instance created despite not setting one explicitly due to the way the
        // interface has been set up
        assertEquals(URI_PATH + "/" + incomingClub.getId().toString(), clubResponse.getLocation().getPath());

        Club createdClub = OBJECT_MAPPER.convertValue(clubResponse.getEntity(), Club.class);
        assertEquals(incomingClub.getId(), createdClub.getId());
    }

    /**
     * given a valid club entity in the request, tests that an updated club entity with update internal fields is
     * upserted in couchbase
     */
    @Test
    public void updateClub_updatesClubInCouchbase() {
        // setup
        UUID existingClubId = UUID.randomUUID();
        Club existingClubInCouchbase = getClubDataStub(existingClubId, true);
        Club incomingClub = ImmutableClub.builder().from(getClubDataStub(existingClubId, false))
                .wageBudget(BigDecimal.valueOf(300))
                .build();
        ArgumentCaptor<ResourceKey> resourceKeyCaptor = ArgumentCaptor.forClass(ResourceKey.class);
        ArgumentCaptor<Club> updatedClubCaptor = ArgumentCaptor.forClass(Club.class);
        when(clubDAO.getDocument(any(), any())).thenReturn(existingClubInCouchbase);

        // execute
        Response clubResponse = clubResource.updateClub(existingClubId, incomingClub);

        // assert
        verify(clubDAO).getDocument(resourceKeyCaptor.capture(), any());
        ResourceKey capturedResourceKey = resourceKeyCaptor.getValue();
        assertEquals(existingClubId, capturedResourceKey.getResourceId());

        verify(clubDAO).updateDocument(eq(capturedResourceKey), updatedClubCaptor.capture());
        Club clubToBeUpdatedInCouchbase = updatedClubCaptor.getValue();
        assertNotNull(clubToBeUpdatedInCouchbase);
        assertEquals(userPrincipal.getEmail(), clubToBeUpdatedInCouchbase.getCreatedBy());
        assertNotEquals(existingClubInCouchbase.getLastModifiedDate(), clubToBeUpdatedInCouchbase.getLastModifiedDate());

        assertEquals(HttpStatus.OK_200, clubResponse.getStatus());
        assertNotNull(clubResponse.getEntity());

        Club clubInResponse = OBJECT_MAPPER.convertValue(clubResponse.getEntity(), Club.class);
        assertEquals(existingClubInCouchbase.getId(), clubInResponse.getId());
        assertEquals(incomingClub.getWageBudget(), clubInResponse.getWageBudget());
        assertEquals(userPrincipal.getEmail(), clubInResponse.getCreatedBy());
    }

    /**
     * given that the request contains a club entity whose ID does not match the existing club's ID, tests that the
     * invalid entity is not upserted in couchbase and a server error response is returned
     */
    @Test
    public void updateClub_incomingClubIdDoesNotMatchExisting() {
        // setup
        UUID existingClubId = UUID.randomUUID();
        Club existingClubInCouchbase = getClubDataStub(existingClubId, true);
        when(clubDAO.getDocument(any(), any())).thenReturn(existingClubInCouchbase);

        UUID incorrectIncomingClubId = UUID.randomUUID();
        Club incomingClub = ImmutableClub.builder()
                .from(existingClubInCouchbase)
                .id(incorrectIncomingClubId)
                .build();

        // execute
        Response clubResponse = clubResource.updateClub(existingClubId, incomingClub);

        // assert
        verify(clubDAO).getDocument(any(), any());
        verify(clubDAO, never()).updateDocument(any(), any());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR_500, clubResponse.getStatus());
        assertTrue(clubResponse.getEntity().toString().contains(incorrectIncomingClubId.toString()));
    }

    /**
     * given a valid club ID, removes the club entity from couchbase
     */
    @Test
    public void deleteClub_removesClubFromCouchbase() {
        // setup
        UUID clubId = UUID.randomUUID();
        ArgumentCaptor<ResourceKey> resourceKeyCaptor = ArgumentCaptor.forClass(ResourceKey.class);

        // execute
        Response clubResponse = clubResource.deleteClub(clubId);

        // assert
        verify(clubDAO).deleteDocument(resourceKeyCaptor.capture());
        ResourceKey capturedResourceKey = resourceKeyCaptor.getValue();
        assertEquals(clubId, capturedResourceKey.getResourceId());

        assertEquals(HttpStatus.NO_CONTENT_204, clubResponse.getStatus());
    }

    @Test
    public void getSquadPlayers_fetchesPlayersFromCouchbase() {
        // setup
        UUID clubId = UUID.randomUUID();
        ImmutableSquadPlayer expectedSquadPlayer = ImmutableSquadPlayer.builder()
                .name("fake player name")
                .country("fake player country")
                .role("fake player role")
                .currentAbility(19)
                .playerId(UUID.randomUUID())
                .build();
        when(clubDAO.getPlayersInClub(eq(clubId))).thenReturn(ImmutableList.of(expectedSquadPlayer));

        // execute
        Response response = clubResource.getSquadPlayers(clubId);

        // assert
        verify(clubDAO).getPlayersInClub(eq(clubId));
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        assertNotNull(response.getEntity());

        List<Object> entityList = OBJECT_MAPPER.convertValue(response.getEntity(), List.class);
        assertFalse(entityList.isEmpty());
        entityList.forEach(entity -> {
            SquadPlayer squadPlayerFromResponse = OBJECT_MAPPER.convertValue(entity, SquadPlayer.class);
            assertNotNull(squadPlayerFromResponse);
            assertEquals(expectedSquadPlayer, squadPlayerFromResponse);
        });
    }

    private Club getClubDataStub(UUID clubId, boolean isExisting) {
        ImmutableClub.Builder clubBuilder = ImmutableClub.builder()
                .name("fake club name")
                .userId(UUID.randomUUID())
                .expenditure(BigDecimal.valueOf(1000))
                .income(BigDecimal.valueOf(2000))
                .transferBudget(BigDecimal.valueOf(500))
                .wageBudget(BigDecimal.valueOf(200));

        if (clubId != null) clubBuilder.id(clubId);

        if (isExisting) {
            clubBuilder.createdBy(USER_EMAIL);

            Instant currentInstant = Instant.now();
            Instant olderInstant = currentInstant.minus(1, ChronoUnit.DAYS);
            clubBuilder.lastModifiedDate(LocalDate.ofInstant(olderInstant, ZoneId.systemDefault()));
        }

        return clubBuilder.build();
    }
}