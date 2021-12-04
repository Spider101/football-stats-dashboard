package com.footballstatsdashboard.resources;

import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
    private static final String USER_EMAIL = "fake email";
    private static final int CURRENT_PLAYER_ABILITY = 19;

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
    public void getClubCetchesClubFromCouchbase() {
        // setup
        UUID clubId = UUID.randomUUID();
        Club clubFromCouchbase = getClubDataStub(clubId, userPrincipal.getId(), false);
        when(clubDAO.getDocument(any(), any())).thenReturn(clubFromCouchbase);

        // execute
        Response clubResponse = clubResource.getClub(clubId);

        // assert
        verify(clubDAO).getDocument(any(), any());
        assertEquals(HttpStatus.OK_200, clubResponse.getStatus());
        assertNotNull(clubResponse.getEntity());

        Club clubFromResponse = OBJECT_MAPPER.convertValue(clubResponse.getEntity(), Club.class);
        assertEquals(clubId, clubFromResponse.getId());
        assertNotNull(clubFromResponse.getUserId());
        assertEquals(userPrincipal.getId(), clubFromResponse.getUserId());
    }

    /**
     * given a runtime exception is thrown by couchbase DAO when club entity is not found, verifies that the same
     * exception is thrown by `getClub` resource method as well
     */
    @Test(expected = RuntimeException.class)
    public void getClubClubNotFoundInCouchbase() {
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
    public void createClubPersistsClubInCouchbase() {
        // setup
        Club incomingClub = getClubDataStub(null, null, false);
        ArgumentCaptor<Club> newClubCaptor = ArgumentCaptor.forClass(Club.class);

        // execute
        Response clubResponse = clubResource.createClub(userPrincipal, incomingClub, uriInfo);

        // assert
        verify(clubDAO).insertDocument(any(), newClubCaptor.capture());
        Club newClub = newClubCaptor.getValue();
        assertNotNull(newClub.getCreatedDate());
        assertNotNull(newClub.getLastModifiedDate());
        assertEquals(userPrincipal.getEmail(), newClub.getCreatedBy());
        assertEquals(userPrincipal.getId(), newClub.getUserId());

        assertEquals(HttpStatus.CREATED_201, clubResponse.getStatus());
        assertNotNull(clubResponse.getEntity());

        // a clubId is set on the club instance created despite not setting one explicitly due to the way the
        // interface has been set up
        assertEquals(URI_PATH + "/" + incomingClub.getId().toString(), clubResponse.getLocation().getPath());

        Club createdClub = OBJECT_MAPPER.convertValue(clubResponse.getEntity(), Club.class);
        assertEquals(incomingClub.getId(), createdClub.getId());
    }

    @Test
    public void createClubWhenClubNameIsEmpty() {
        // setup
        Club incomingClubWithNoName = ImmutableClub.builder()
                .from(getClubDataStub(null, null, false))
                .name("")
                .build();

        // execute
        Response clubResponse = clubResource.createClub(userPrincipal, incomingClubWithNoName, uriInfo);

        // assert
        verify(clubDAO, never()).insertDocument(any(), any());
        assertNotNull(clubResponse);
        assertEquals(HttpStatus.BAD_REQUEST_400, clubResponse.getStatus());
    }

    /**
     * given a valid club entity in the request, tests that an updated club entity with update internal fields is
     * upserted in couchbase
     */
    @Test
    public void updateClubUpdatesClubInCouchbase() {
        // setup
        UUID existingClubId = UUID.randomUUID();
        Club existingClubInCouchbase = getClubDataStub(existingClubId, userPrincipal.getId(), true);
        BigDecimal updatedWageBudget = existingClubInCouchbase.getWageBudget().add(new BigDecimal("100"));
        Club incomingClub = ImmutableClub.builder()
                .from(getClubDataStub(existingClubId, null, false))
                .wageBudget(updatedWageBudget)
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
        assertEquals(userPrincipal.getId(), clubToBeUpdatedInCouchbase.getUserId());
        assertEquals(LocalDate.now(), clubToBeUpdatedInCouchbase.getLastModifiedDate());

        assertEquals(HttpStatus.OK_200, clubResponse.getStatus());
        assertNotNull(clubResponse.getEntity());

        Club clubInResponse = OBJECT_MAPPER.convertValue(clubResponse.getEntity(), Club.class);
        assertEquals(existingClubInCouchbase.getId(), clubInResponse.getId());
        assertEquals(updatedWageBudget, clubInResponse.getWageBudget());
        assertEquals(userPrincipal.getEmail(), clubInResponse.getCreatedBy());
    }

    /**
     * given that the request contains a club entity whose ID does not match the existing club's ID, tests that the
     * invalid entity is not upserted in couchbase and a server error response is returned
     */
    @Test
    public void updateClubWhenIncomingClubIdDoesNotMatchExisting() {
        // setup
        UUID existingClubId = UUID.randomUUID();
        Club existingClubInCouchbase = getClubDataStub(existingClubId, userPrincipal.getId(), true);
        when(clubDAO.getDocument(any(), any())).thenReturn(existingClubInCouchbase);

        UUID incorrectIncomingClubId = UUID.randomUUID();
        Club incomingClub = ImmutableClub.builder()
                .from(existingClubInCouchbase)
                .id(incorrectIncomingClubId) // override the incoming club data's ID to be incorrect
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
    public void deleteClubRemovesClubFromCouchbase() {
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

    /**
     * given a valid user entity as the auth principal, tests that all clubs associated with the user is fetched from
     * couchbase and returned in the response
     */
    @Test
    public void getClubsByUserIdFetchesAllClubsForUser() {
        // setup
        int numberOfClubs = 2;
        UUID userId = userPrincipal.getId();
        List<Club> mockClubData = getBulkClubDataStub(numberOfClubs, userId);
        when(clubDAO.getClubsByUserId(any())).thenReturn(mockClubData);

        // execute
        Response response = clubResource.getClubsByUserId(userPrincipal);

        // assert
        verify(clubDAO).getClubsByUserId(eq(userId));
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        assertNotNull(response.getEntity());

        TypeReference<List<Club>> clubListTypeRef = new TypeReference<>() { };
        List<Club> clubList = OBJECT_MAPPER.convertValue(response.getEntity(), clubListTypeRef);
        assertFalse(clubList.isEmpty());

        for (int idx = 0; idx < clubList.size(); idx++) {
            assertEquals(userId, clubList.get(idx).getUserId());
            assertEquals(mockClubData.get(idx).getName(), clubList.get(idx).getName());
        }
    }

    /**
     * given a valid user entity as the auth principal but no there are no club entities associated with it, tests that
     * an empty list is returned in the response
     */
    @Test
    public void getClubsByUserIdReturnsEmptyListWhenNoClubsAreAssociatedWithUser() {
        // setup
        int numberOfClubs = 0;
        UUID userId = userPrincipal.getId();
        List<Club> mockClubData = getBulkClubDataStub(numberOfClubs, userId);
        when(clubDAO.getClubsByUserId(any())).thenReturn(mockClubData);

        // execute
        Response response = clubResource.getClubsByUserId(userPrincipal);

        // assert
        verify(clubDAO).getClubsByUserId(eq(userId));
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        assertNotNull(response.getEntity());

        TypeReference<List<Club>> clubListTypeRef = new TypeReference<>() { };
        List<Club> clubList = OBJECT_MAPPER.convertValue(response.getEntity(), clubListTypeRef);
        assertTrue(clubList.isEmpty());
    }

    @Test
    public void getSquadPlayersFetchesPlayersFromCouchbase() {
        // setup
        UUID clubId = UUID.randomUUID();
        ImmutableSquadPlayer expectedSquadPlayer = ImmutableSquadPlayer.builder()
                .name("fake player name")
                .country("fake player country")
                .role("fake player role")
                .currentAbility(CURRENT_PLAYER_ABILITY)
                .recentForm(new ArrayList<>())
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

        TypeReference<List<SquadPlayer>> squadPlayerListTypeRef = new TypeReference<>() { };
        List<SquadPlayer> squadPlayersFromResponse = OBJECT_MAPPER.convertValue(response.getEntity(),
                squadPlayerListTypeRef);
        assertFalse(squadPlayersFromResponse.isEmpty());
        squadPlayersFromResponse.forEach(squadPlayerFromResponse -> {
            assertNotNull(squadPlayerFromResponse);
            assertEquals(expectedSquadPlayer, squadPlayerFromResponse);
        });
    }

    private Club getClubDataStub(UUID clubId, UUID userId, boolean isExisting) {
        ImmutableClub.Builder clubBuilder = ImmutableClub.builder()
                .name("fake club name")
                .expenditure(new BigDecimal("1000"))
                .income(new BigDecimal("2000"))
                .transferBudget(new BigDecimal("500"))
                .wageBudget(new BigDecimal("200"));

        if (clubId != null) clubBuilder.id(clubId);

        if (userId != null) {
            clubBuilder.userId(userId);
            clubBuilder.createdBy(USER_EMAIL);
        }

        if (isExisting) {
            Instant currentInstant = Instant.now();
            Instant olderInstant = currentInstant.minus(1, ChronoUnit.DAYS);
            clubBuilder.lastModifiedDate(LocalDate.ofInstant(olderInstant, ZoneId.systemDefault()));
        }

        return clubBuilder.build();
    }

    private List<Club> getBulkClubDataStub(int numClubs, UUID userId) {
        return IntStream.range(0, numClubs).mapToObj(i ->
                ImmutableClub.builder()
                        .userId(userId)
                        .name("fake club name " + i)
                        .transferBudget(BigDecimal.ONE)
                        .wageBudget(BigDecimal.ONE)
                        .income(BigDecimal.ONE)
                        .expenditure(BigDecimal.ONE)
                        .build()
        ).collect(Collectors.toList());
    }
}