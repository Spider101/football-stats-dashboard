package com.footballstatsdashboard.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.footballstatsdashboard.ClubDataProvider;
import com.footballstatsdashboard.api.model.Club;
import com.footballstatsdashboard.api.model.ImmutableUser;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.api.model.club.ClubSummary;
import com.footballstatsdashboard.api.model.club.ImmutableSquadPlayer;
import com.footballstatsdashboard.api.model.club.SquadPlayer;
import com.footballstatsdashboard.core.exceptions.ServiceException;
import com.footballstatsdashboard.services.ClubService;
import com.footballstatsdashboard.services.FileUploadService;
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
import static org.junit.Assert.assertThrows;
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
    private FileUploadService fileUploadService;

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

        clubResource = new ClubResource(clubService, fileUploadService);
        verify(fileUploadService).initializeService();
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
        when(clubService.getClub(eq(clubId), any())).thenReturn(existingClub);

        // execute
        Response clubResponse = clubResource.getClub(userPrincipal, clubId);

        // assert
        verify(clubService).getClub(any(), any());
        assertEquals(HttpStatus.OK_200, clubResponse.getStatus());
        assertNotNull(clubResponse.getEntity());

        Club clubFromResponse = OBJECT_MAPPER.convertValue(clubResponse.getEntity(), Club.class);
        assertEquals(clubId, clubFromResponse.getId());
        assertNotNull(clubFromResponse.getUserId());
        assertEquals(userPrincipal.getId(), clubFromResponse.getUserId());
    }

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
        when(fileUploadService.doesFileExist(eq(incomingClub.getLogo()))).thenReturn(true);

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
     * given a valid club entity in the request with an invalid club logo file key, tests that the club data is
     * not persisted and a service exception is thrown
     */
    @Test
    public void createClubWithInvalidClubLogoFileKey() {
        // assert
        Club incomingClub = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .withIncome()
                .withCustomClubLogo("../../maliciousFileKey.png")
                .withExpenditure()
                .build();
        when(fileUploadService.doesFileExist(eq(incomingClub.getLogo()))).thenReturn(false);

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> clubResource.createClub(userPrincipal, incomingClub, uriInfo));

        // assert
        verify(fileUploadService).doesFileExist(anyString());
        verify(clubService, never()).createClub(any(), any(), anyString());
        assertEquals(HttpStatus.NOT_FOUND_404, serviceException.getResponseStatus());
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
        when(clubService.getClub(any(), any())).thenReturn(existingClub);

        BigDecimal updatedWageBudget = existingClub.getWageBudget().add(new BigDecimal("100"));
        BigDecimal updatedTransferBudget = existingClub.getTransferBudget().add(new BigDecimal("100"));
        BigDecimal totalFunds = updatedTransferBudget.add(updatedWageBudget);
        Club incomingClubBase = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .withId(existingClubId)
                .build();
        Club incomingClub = ClubDataProvider.ModifiedClubBuilder.builder()
                .from(incomingClubBase)
                .withUpdatedTransferBudget(updatedTransferBudget)
                .withUpdatedWageBudget(updatedWageBudget)
                .withUpdatedManagerFunds(totalFunds)
                .build();

        Club updatedClub = ClubDataProvider.ModifiedClubBuilder.builder()
                .from(existingClub)
                .withUpdatedTransferBudget(updatedTransferBudget)
                .withUpdatedWageBudget(updatedWageBudget)
                .withUpdatedManagerFunds(totalFunds)
                .build();
        when(clubService.updateClub(any(), any(), any())).thenReturn(updatedClub);

        when(fileUploadService.doesFileExist(eq(incomingClub.getLogo()))).thenReturn(true);

        // execute
        Response clubResponse = clubResource.updateClub(userPrincipal, existingClubId, incomingClub);

        // assert
        verify(clubService).getClub(eq(existingClubId), any());
        verify(clubService).updateClub(eq(incomingClub), eq(existingClub), eq(existingClubId));

        assertEquals(HttpStatus.OK_200, clubResponse.getStatus());
        assertNotNull(clubResponse.getEntity());

        Club clubInResponse = OBJECT_MAPPER.convertValue(clubResponse.getEntity(), Club.class);
        assertEquals(existingClub.getId(), clubInResponse.getId());

        assertEquals(userPrincipal.getId(), clubInResponse.getUserId());
        assertEquals(userPrincipal.getEmail(), clubInResponse.getCreatedBy());
        assertEquals(LocalDate.now(), clubInResponse.getLastModifiedDate());
    }

    /**
     * given a valid club entity in the request with an invalid club logo file key, tests that the club data is
     * not updated and a service exception is thrown
     */
    @Test
    public void updateClubWhenIncomingClubHasInvalidClubLogoFileKey() {
        // setup
        UUID existingClubId = UUID.randomUUID();
        Club existingClub = ClubDataProvider.ClubBuilder.builder()
                .isExisting(true)
                .existingUserId(userPrincipal.getId())
                .withId(existingClubId)
                .withIncome()
                .withExpenditure()
                .build();

        BigDecimal updatedWageBudget = existingClub.getWageBudget().add(new BigDecimal("100"));
        BigDecimal updatedTransferBudget = existingClub.getTransferBudget().add(new BigDecimal("100"));
        BigDecimal totalFunds = updatedTransferBudget.add(updatedWageBudget);
        Club incomingClubBase = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .withId(existingClubId)
                .build();
        Club incomingClub = ClubDataProvider.ModifiedClubBuilder.builder()
                .from(incomingClubBase)
                .withUpdatedClubLogo("../../newInvalidFileKey.png")
                .withUpdatedTransferBudget(updatedTransferBudget)
                .withUpdatedWageBudget(updatedWageBudget)
                .withUpdatedManagerFunds(totalFunds)
                .build();
        when(fileUploadService.doesFileExist(eq(incomingClub.getLogo()))).thenReturn(false);

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> clubResource.updateClub(userPrincipal, existingClubId, incomingClub));

        // assert
        verify(fileUploadService).doesFileExist(anyString());
        verify(clubService, never()).getClub(any(), any());
        verify(clubService, never()).updateClub(any(), any(), any());
        assertEquals(HttpStatus.NOT_FOUND_404, serviceException.getResponseStatus());
    }

    /**
     * given that the request contains a club entity whose ID does not match the existing club's ID, tests that the
     * associated club data is not updated and a service exception is thrown instead
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
        when(clubService.getClub(any(), any())).thenReturn(existingClub);

        UUID incorrectIncomingClubId = UUID.randomUUID();
        Club incomingClub = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .withId(incorrectIncomingClubId)
                .build();

        when(fileUploadService.doesFileExist(eq(incomingClub.getLogo()))).thenReturn(true);

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> clubResource.updateClub(userPrincipal, existingClubId, incomingClub));

        // assert
        verify(clubService).getClub(any(), any());
        verify(clubService, never()).updateClub(any(), any(), any());
        assertEquals(HttpStatus.CONFLICT_409, serviceException.getResponseStatus());
    }

    /**
     * given that the request contains a club that does not belong to the current user, tests that the club data is not
     * updated and a service exception is thrown instead
     */
    @Test
    public void updateClubWhenClubDoesNotBelongToUser() {
        // setup
        UUID existingClubId = UUID.randomUUID();
        Club existingClub = ClubDataProvider.ClubBuilder.builder()
                .isExisting(true)
                .existingUserId(userPrincipal.getId())
                .withId(existingClubId)
                .withIncome()
                .withExpenditure()
                .build();
        when(clubService.getClub(eq(existingClubId), eq(userPrincipal.getId())))
                .thenThrow(new ServiceException(HttpStatus.NOT_FOUND_404, "No club found!"));

        BigDecimal updatedWageBudget = existingClub.getWageBudget().add(new BigDecimal("100"));
        BigDecimal updatedTransferBudget = existingClub.getTransferBudget().add(new BigDecimal("100"));
        BigDecimal totalFunds = updatedTransferBudget.add(updatedWageBudget);
        Club incomingClubBase = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .withId(existingClubId)
                .build();
        Club incomingClub = ClubDataProvider.ModifiedClubBuilder.builder()
                .from(incomingClubBase)
                .withUpdatedTransferBudget(updatedTransferBudget)
                .withUpdatedWageBudget(updatedWageBudget)
                .withUpdatedManagerFunds(totalFunds)
                .build();

        when(fileUploadService.doesFileExist(eq(incomingClub.getLogo()))).thenReturn(true);

        // execute
        assertThrows(ServiceException.class,
                () -> clubResource.updateClub(userPrincipal, existingClubId, incomingClub));

        // assert
        verify(clubService).getClub(any(), any());
        verify(clubService, never()).updateClub(any(), any(), any());
    }

    /**
     * given a valid club ID, tests that the club data is removed and a 204 No Content response is returned
     */
    @Test
    public void deleteClubRemovesClubData() {
        // setup
        UUID clubId = UUID.randomUUID();

        // execute
        Response clubResponse = clubResource.deleteClub(userPrincipal, clubId);

        // assert
        verify(clubService).deleteClub(any(), any());
        assertEquals(HttpStatus.NO_CONTENT_204, clubResponse.getStatus());
    }

    /**
     * given a valid user entity as the auth principal, tests that all clubs associated with the user is fetched and
     * returned in the response
     */
    @Test
    public void getClubsByUserIdFetchesAllClubsForUser() {
        // setup
        UUID userId = userPrincipal.getId();
        // TODO: 04/03/22 update data provider to include club logo file key when ready
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
                .countryFlag("fake country flag url")
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