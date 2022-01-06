package com.footballstatsdashboard.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.footballstatsdashboard.api.model.ImmutableUser;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.api.model.club.Club;
import com.footballstatsdashboard.api.model.club.ImmutableClub;
import com.footballstatsdashboard.api.model.club.ImmutableSquadPlayer;
import com.footballstatsdashboard.api.model.club.SquadPlayer;
import com.footballstatsdashboard.services.ClubService;
import com.google.common.collect.ImmutableList;
import io.dropwizard.jackson.Jackson;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
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
import static org.mockito.ArgumentMatchers.anyString;
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
    private ClubService clubService;

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

        clubResource = new ClubResource(clubService);
    }

    /**
     * given a valid club id, tests that the club entity is successfully fetched and returned in the response
     */
    @Test
    public void getClubFetchesClubData() {
        // setup
        UUID clubId = UUID.randomUUID();
        Club clubFromCouchbase = getClubDataStub(clubId, userPrincipal.getId(), true);
        when(clubService.getClub(eq(clubId))).thenReturn(clubFromCouchbase);

        // execute
        Response clubResponse = clubResource.getClub(clubId);

        // assert
        verify(clubService).getClub(any());
        assertEquals(HttpStatus.OK_200, clubResponse.getStatus());
        assertNotNull(clubResponse.getEntity());

        Club clubFromResponse = OBJECT_MAPPER.convertValue(clubResponse.getEntity(), Club.class);
        assertEquals(clubId, clubFromResponse.getId());
        assertNotNull(clubFromResponse.getUserId());
        assertEquals(userPrincipal.getId(), clubFromResponse.getUserId());
    }

    // TODO: 1/3/2022 test that the runtime exception thrown when a couchbase document is not found results in a 404

    /**
     * given a valid club entity in the request, tests that the club data is successfully persisted
     */
    @Test
    public void createClubPersistsClubData() {
        // setup
        Club incomingClub = getClubDataStub(null, null, false);
        Club createdClub = getClubDataStub(incomingClub.getId(), userPrincipal.getId(), true);
        when(clubService.createClub(any(), any(), anyString())).thenReturn(createdClub);

        // execute
        Response clubResponse = clubResource.createClub(userPrincipal, incomingClub, uriInfo);

        // assert
        verify(clubService).createClub(eq(incomingClub), eq(userPrincipal.getId()), eq(userPrincipal.getEmail()));
        assertEquals(HttpStatus.CREATED_201, clubResponse.getStatus());
        assertNotNull(clubResponse.getEntity());

        // a clubId is set on the club instance created despite not setting one explicitly due to the way the
        // interface has been set up
        assertEquals(URI_PATH + "/" + incomingClub.getId().toString(), clubResponse.getLocation().getPath());
    }

    /**
     * given that the request contains a club entity with an empty club name, tests that no data is persisted and a 400
     * Bad Request response status is returned
     */
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
        verify(clubService, never()).createClub(any(), any(), anyString());
        assertNotNull(clubResponse);
        assertEquals(HttpStatus.BAD_REQUEST_400, clubResponse.getStatus());
    }

    /**
     * given a valid club entity in the request, tests that the corresponding club data is updated
     */
    @Test
    public void updateClubUpdatesClubData() {
        // setup
        UUID existingClubId = UUID.randomUUID();
        Club existingClubInCouchbase = getClubDataStub(existingClubId, userPrincipal.getId(), true);
        when(clubService.getClub(any())).thenReturn(existingClubInCouchbase);

        BigDecimal updatedWageBudget = existingClubInCouchbase.getWageBudget().add(new BigDecimal("100"));
        Club incomingClub = ImmutableClub.builder()
                .from(getClubDataStub(existingClubId, null, false))
                .wageBudget(updatedWageBudget)
                .build();
        Club updatedClubInCouchbase = ImmutableClub.builder()
                .from(existingClubInCouchbase)
                .wageBudget(updatedWageBudget)
                .lastModifiedDate(LocalDate.now())
                .build();
        when(clubService.updateClub(any(), any(), any())).thenReturn(updatedClubInCouchbase);

        // execute
        Response clubResponse = clubResource.updateClub(existingClubId, incomingClub);

        // assert
        verify(clubService).getClub(eq(existingClubId));
        verify(clubService).updateClub(eq(incomingClub), eq(existingClubInCouchbase), eq(existingClubId));

        assertEquals(HttpStatus.OK_200, clubResponse.getStatus());
        assertNotNull(clubResponse.getEntity());

        Club clubInResponse = OBJECT_MAPPER.convertValue(clubResponse.getEntity(), Club.class);
        assertEquals(existingClubInCouchbase.getId(), clubInResponse.getId());
        assertEquals(updatedWageBudget, clubInResponse.getWageBudget());

        assertEquals(userPrincipal.getId(), clubInResponse.getUserId());
        assertEquals(userPrincipal.getEmail(), clubInResponse.getCreatedBy());
        assertEquals(userPrincipal.getEmail(), clubInResponse.getCreatedBy());
        assertEquals(LocalDate.now(), clubInResponse.getLastModifiedDate());
    }

    /**
     * given that the request contains a club entity whose ID does not match the existing club's ID, tests that the
     * associated club data is not updated and a server error response is returned
     */
    @Test
    public void updateClubWhenIncomingClubIdDoesNotMatchExisting() {
        // setup
        UUID existingClubId = UUID.randomUUID();
        Club existingClubInCouchbase = getClubDataStub(existingClubId, userPrincipal.getId(), true);
        when(clubService.getClub(any())).thenReturn(existingClubInCouchbase);

        UUID incorrectIncomingClubId = UUID.randomUUID();
        Club incomingClub = ImmutableClub.builder()
                .from(existingClubInCouchbase)
                .id(incorrectIncomingClubId) // override the incoming club data's ID to be incorrect
                .build();

        // execute
        Response clubResponse = clubResource.updateClub(existingClubId, incomingClub);

        // assert
        verify(clubService).getClub(any());
        verify(clubService, never()).updateClub(any(), any(), any());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR_500, clubResponse.getStatus());
        assertTrue(clubResponse.getEntity().toString().contains(incorrectIncomingClubId.toString()));
    }

    /**
     * given a valid club ID, removes the club data and a 204 No Content response is returned
     */
    @Test
    public void deleteClubRemovesClubData() {
        // setup
        UUID clubId = UUID.randomUUID();

        // execute
        Response clubResponse = clubResource.deleteClub(clubId);

        // assert
        verify(clubService).deleteClub(eq(clubId));
        assertEquals(HttpStatus.NO_CONTENT_204, clubResponse.getStatus());
    }

    // TODO: 1/6/2022 add test when trying to delete a club not belonging to user

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
        when(clubService.getClubsByUserId(any())).thenReturn(mockClubData);

        // execute
        Response response = clubResource.getClubsByUserId(userPrincipal);

        // assert
        verify(clubService).getClubsByUserId(eq(userId));
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
     * given a valid club ID, fetches player data for all players associated with the club
     */
    @Test
    public void getSquadPlayersFetchesAllPlayersInClub() {
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
        when(clubService.getSquadPlayers(eq(clubId))).thenReturn(ImmutableList.of(expectedSquadPlayer));

        // execute
        Response response = clubResource.getSquadPlayers(clubId);

        // assert
        verify(clubService).getSquadPlayers(eq(clubId));
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        assertNotNull(response.getEntity());

        TypeReference<List<SquadPlayer>> squadPlayerListTypeRef = new TypeReference<>() { };
        List<SquadPlayer> squadPlayersFromResponse = OBJECT_MAPPER.convertValue(response.getEntity(),
                squadPlayerListTypeRef);
        assertFalse(squadPlayersFromResponse.isEmpty());
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