package com.footballstatsdashboard.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.footballstatsdashboard.ClubDataProvider;
import com.footballstatsdashboard.api.model.ImmutableUser;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.api.model.Club;
import com.footballstatsdashboard.api.model.club.ClubSummary;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        Club existingClub = ClubDataProvider.ClubBuilder.builder()
                .isExisting(true)
                .existingUserId(userPrincipal.getId())
                .withId(clubId)
                .withIncome()
                .withExpenditure()
                .build();
        when(clubService.getClub(eq(clubId))).thenReturn(existingClub);

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
        Club incomingClub = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .withIncome()
                .withExpenditure()
                .build();
        Club createdClub = ClubDataProvider.ClubBuilder.builder()
                .isExisting(true)
                .existingUserId(userPrincipal.getId())
                .withId(incomingClub.getId())
                .withIncome()
                .withExpenditure()
                .build();
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
        Club incomingClubWithNoName = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .customClubName("")
                .withIncome()
                .withExpenditure()
                .build();

        // execute
        Response clubResponse = clubResource.createClub(userPrincipal, incomingClubWithNoName, uriInfo);

        // assert
        verify(clubService, never()).createClub(any(), any(), anyString());
        assertNotNull(clubResponse);
        assertEquals(HttpStatus.BAD_REQUEST_400, clubResponse.getStatus());
    }

    /**
     * given that the request contains a club entity without valid income data, tests that no data is persisted and a
     * 400 Bad Request response status is returned
     */
    @Test
    public void createClubWithoutIncomeData() {
        // setup
        Club incomingClubWithNoIncomeData = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .withExpenditure()
                .build();

        // execute
        Response clubResponse = clubResource.createClub(userPrincipal, incomingClubWithNoIncomeData, uriInfo);

        // assert
        verify(clubService, never()).createClub(any(), any(), anyString());
        assertNotNull(clubResponse);
        assertEquals(HttpStatus.BAD_REQUEST_400, clubResponse.getStatus());
    }

    /**
     * given that the request contains a club entity without valid income data, tests that no data is persisted and a
     * 400 Bad Request response status is returned
     */
    @Test
    public void createClubWithoutExpenditureData() {
        // setup
        Club incomingClubWithNoExpenditureData = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .withIncome()
                .build();

        // execute
        Response clubResponse = clubResource.createClub(userPrincipal, incomingClubWithNoExpenditureData, uriInfo);

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
        Club existingClub = ClubDataProvider.ClubBuilder.builder()
                .isExisting(true)
                .existingUserId(userPrincipal.getId())
                .withId(existingClubId)
                .withIncome()
                .withExpenditure()
                .build();
        when(clubService.getClub(any())).thenReturn(existingClub);

        BigDecimal updatedWageBudget = existingClub.getWageBudget().add(new BigDecimal("100"));
        Club incomingClubBase = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .withId(existingClubId)
                .build();
        Club incomingClub = ClubDataProvider.ModifiedClubBuilder.builder()
                .from(incomingClubBase)
                .withUpdatedWageBudget(updatedWageBudget)
                .build();

        Club updatedClub = ClubDataProvider.ModifiedClubBuilder.builder()
                .from(existingClub)
                .withUpdatedWageBudget(updatedWageBudget)
                .build();
        when(clubService.updateClub(any(), any(), any())).thenReturn(updatedClub);

        // execute
        Response clubResponse = clubResource.updateClub(existingClubId, incomingClub);

        // assert
        verify(clubService).getClub(eq(existingClubId));
        verify(clubService).updateClub(eq(incomingClub), eq(existingClub), eq(existingClubId));

        assertEquals(HttpStatus.OK_200, clubResponse.getStatus());
        assertNotNull(clubResponse.getEntity());

        Club clubInResponse = OBJECT_MAPPER.convertValue(clubResponse.getEntity(), Club.class);
        assertEquals(existingClub.getId(), clubInResponse.getId());
        assertEquals(updatedWageBudget, clubInResponse.getWageBudget());

        assertEquals(userPrincipal.getId(), clubInResponse.getUserId());
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
        Club existingClub = ClubDataProvider.ClubBuilder.builder()
                .isExisting(true)
                .existingUserId(userPrincipal.getId())
                .withId(existingClubId)
                .withIncome()
                .withExpenditure()
                .build();
        when(clubService.getClub(any())).thenReturn(existingClub);

        UUID incorrectIncomingClubId = UUID.randomUUID();
        Club incomingClub = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .withId(incorrectIncomingClubId)
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
     * given a valid user entity as the auth principal, tests that all clubs associated with the user is fetched and
     * returned in the response
     */
    @Test
    public void getClubsByUserIdFetchesAllClubsForUser() {
        // setup
        UUID userId = userPrincipal.getId();
        List<ClubSummary> mockClubData = ClubDataProvider.getAllClubSummariesForUser(userId);
        when(clubService.getClubSummariesByUserId(any())).thenReturn(mockClubData);

        // execute
        Response response = clubResource.getClubsByUserId(userPrincipal);

        // assert
        verify(clubService).getClubSummariesByUserId(eq(userId));
        assertNotNull(response);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        assertNotNull(response.getEntity());

        TypeReference<List<ClubSummary>> clubSummaryListTypeRef = new TypeReference<>() {
        };
        List<ClubSummary> clubSummaries = OBJECT_MAPPER.convertValue(response.getEntity(), clubSummaryListTypeRef);
        assertFalse(clubSummaries.isEmpty());

        for (int idx = 0; idx < clubSummaries.size(); idx++) {
            assertEquals(mockClubData.get(idx).getName(), clubSummaries.get(idx).getName());
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

        TypeReference<List<SquadPlayer>> squadPlayerListTypeRef = new TypeReference<>() {
        };
        List<SquadPlayer> squadPlayersFromResponse = OBJECT_MAPPER.convertValue(response.getEntity(),
                squadPlayerListTypeRef);
        assertFalse(squadPlayersFromResponse.isEmpty());
    }
}