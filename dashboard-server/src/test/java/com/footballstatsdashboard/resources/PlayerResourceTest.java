package com.footballstatsdashboard.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.footballstatsdashboard.ClubDataProvider;
import com.footballstatsdashboard.PlayerDataProvider;
import com.footballstatsdashboard.api.model.ImmutableUser;
import com.footballstatsdashboard.api.model.Player;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.api.model.Club;
import com.footballstatsdashboard.core.exceptions.ServiceException;
import com.footballstatsdashboard.services.ClubService;
import com.footballstatsdashboard.services.PlayerService;
import io.dropwizard.jackson.Jackson;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for player resource
 */
public class PlayerResourceTest {
    private static final String URI_PATH = "/players";
    private static final int UPDATED_PLAYER_ABILITY = 25;
    private static final int UPDATED_PLAYER_SPRINT_SPEED = 87;
    private static final ObjectMapper OBJECT_MAPPER = Jackson.newObjectMapper().copy();

    private PlayerResource playerResource;
    private User userPrincipal;

    @Mock
    private PlayerService playerService;

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

        playerResource = new PlayerResource(playerService, clubService);
        userPrincipal = ImmutableUser.builder()
                .email("fake email")
                // other details are not required for the purposes of this test so using empty strings
                .password("")
                .firstName("")
                .lastName("")
                .build();
    }

    /**
     * given a valid player id, tests that the player data is successfully fetched and returned in the response
     */
    @Test
    public void getPlayerSuccessfullyFetchesPlayerData() {
        // setup
        UUID playerId = UUID.randomUUID();
        Player playerToBeFetched = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(true)
                .withId(playerId)
                .withMetadata()
                .withAbility()
                .withRoles()
                .withAttributes()
                .build();
        when(playerService.getPlayer(eq(playerId))).thenReturn(playerToBeFetched);

        // execute
        Response playerResponse = playerResource.getPlayer(playerId);

        // assert
        verify(playerService).getPlayer(any());
        assertEquals(HttpStatus.OK_200, playerResponse.getStatus());
        assertNotNull(playerResponse.getEntity());

        Player playerFromResponse = OBJECT_MAPPER.convertValue(playerResponse.getEntity(), Player.class);
        assertEquals(playerId, playerFromResponse.getId());
    }

    /**
     * given a valid player entity in the request, tests that the player data is successfully persisted
     */
    @Test
    public void createPlayerPersistsPlayerData() throws IOException {
        // setup
        Player incomingPlayer = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(false)
                .withMetadata()
                .withRoles()
                .withAttributes()
                .build();
        Player createdPlayer = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(true)
                .withId(incomingPlayer.getId())
                .withMetadata()
                .withAbility()
                .withRoles()
                .withAttributes()
                .build();
        when(playerService.createPlayer(any(), any(), anyString())).thenReturn(createdPlayer);

        Club existingClub = ClubDataProvider.ClubBuilder.builder()
                .isExisting(true)
                .existingUserId(userPrincipal.getId())
                .withId(UUID.randomUUID())
                .build();
        when(clubService.getClub(any(), any())).thenReturn(existingClub);

        // execute
        Response playerResponse = playerResource.createPlayer(userPrincipal, incomingPlayer, uriInfo);

        // assert
        verify(playerService).createPlayer(eq(incomingPlayer), eq(existingClub), eq(userPrincipal.getEmail()));
        assertEquals(HttpStatus.CREATED_201, playerResponse.getStatus());
        assertNotNull(playerResponse.getEntity());
        // a playerId is set on the player instance created despite not setting one explicitly due to the way the
        // interface has been set up
        assertEquals(URI_PATH + "/" + incomingPlayer.getId().toString(), playerResponse.getLocation().getPath());
    }

    /**
     * given a valid player entity in the request with a club id for a club that doesn't exist, tests that no player
     * data is created and a service exception is thrown instead
     */
    @Test
    public void createPlayerWhenClubForPlayerDoesNotExist() throws IOException {
        // setup
        Player incomingPlayer = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(false)
                .withMetadata()
                .withRoles()
                .withAttributes()
                .build();
        when(clubService.getClub(eq(incomingPlayer.getClubId()), eq(userPrincipal.getId())))
                .thenThrow(new ServiceException(HttpStatus.NOT_FOUND_404, "No player found!"));

        // execute
        assertThrows(ServiceException.class, () -> playerResource.createPlayer(userPrincipal, incomingPlayer, uriInfo));

        // assert
        verify(clubService).getClub(any(), any());
        verify(playerService, never()).createPlayer(any(), any(), anyString());
    }

    /**
     * given a valid player entity in the request with a club id for a club that the current user doesn't have access
     * to, tests that no player data is created and a service exception is thrown instead
     */
    @Test
    public void createPlayerWhenClubForPlayerIsNotAccessible() throws IOException {
        // setup
        Player incomingPlayer = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(false)
                .withMetadata()
                .withRoles()
                .withAttributes()
                .build();
        when(clubService.getClub(eq(incomingPlayer.getClubId()), eq(userPrincipal.getId())))
                .thenThrow(new ServiceException(HttpStatus.FORBIDDEN_403, "User does not have access to club!"));

        // execute
        assertThrows(ServiceException.class, () -> playerResource.createPlayer(userPrincipal, incomingPlayer, uriInfo));

        // assert
        verify(clubService).getClub(any(), any());
        verify(playerService, never()).createPlayer(any(), any(), anyString());
    }

    /**
     * given a valid player entity in the request, tests that the corresponding player data is updated
     */
    @Test
    public void updatePlayerUpdatesPlayerData() {
        // setup
        String updatedPlayerName = "updated name";
        String updatedAttributeName = "sprint speed";
        String updatedRoleName = "updated player role";

        UUID existingPlayerId = UUID.randomUUID();
        Player existingPlayer = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(true)
                .withId(existingPlayerId)
                .withMetadata()
                .withAbility()
                .withRoles()
                .withAttributes()
                .build();
        when(playerService.getPlayer(any())).thenReturn(existingPlayer);

        when(clubService.doesClubBelongToUser(eq(existingPlayer.getClubId()), eq(userPrincipal.getId())))
                .thenReturn(true);

        Player updatedPlayer = PlayerDataProvider.ModifiedPlayerBuilder.builder()
                .from(existingPlayer)
                .withUpdatedNameInMetadata(updatedPlayerName)
                .withUpdatedCurrentAbility(UPDATED_PLAYER_ABILITY)
                .withUpdatedRoleName(updatedRoleName)
                .withUpdatedAttributeValue(updatedAttributeName, UPDATED_PLAYER_SPRINT_SPEED)
                .build();
        when(playerService.updatePlayer(any(), any(), any())).thenReturn(updatedPlayer);

        Player incomingPlayerBase = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(false)
                .withId(existingPlayerId)
                .withMetadata()
                .withAbility()
                .withRoles()
                .withAttributes()
                .build();
        Player incomingPlayer = PlayerDataProvider.ModifiedPlayerBuilder.builder()
                .from(incomingPlayerBase)
                .withUpdatedNameInMetadata(updatedPlayerName)
                .withUpdatedRoleName(updatedRoleName)
                .withUpdatedAttributeValue(updatedAttributeName, UPDATED_PLAYER_SPRINT_SPEED)
                .build();

        // execute
        Response playerResponse = playerResource.updatePlayer(userPrincipal, existingPlayerId, incomingPlayer);

        // assert
        verify(playerService).getPlayer(eq(existingPlayerId));
        verify(clubService).doesClubBelongToUser(any(), any());
        verify(playerService).updatePlayer(any(), any(), eq(existingPlayerId));

        assertEquals(HttpStatus.OK_200, playerResponse.getStatus());
        assertNotNull(playerResponse.getEntity());

        Player playerInResponse = OBJECT_MAPPER.convertValue(playerResponse.getEntity(), Player.class);
        assertEquals(updatedPlayer.getId(), playerInResponse.getId());
        assertEquals(updatedPlayer.getMetadata(), playerInResponse.getMetadata());
        assertEquals(updatedPlayer.getRoles(), playerInResponse.getRoles());
        assertEquals(updatedPlayer.getAbility(), playerInResponse.getAbility());

        assertEquals(userPrincipal.getEmail(), playerInResponse.getCreatedBy());
        assertEquals(LocalDate.now(), playerInResponse.getLastModifiedDate());
    }

    /**
     * given a valid player entity in the request, tests that if the player data does not already exist, no player data
     * is updated and a service exception is thrown instead
     */
    @Test
    public void updatePlayerWhenPlayerDoesNotExist() {
        // setup
        String updatedPlayerName = "updated name";
        String updatedAttributeName = "sprint speed";
        String updatedRoleName = "updated player role";
        UUID existingPlayerId = UUID.randomUUID();

        when(playerService.getPlayer(eq(existingPlayerId)))
                .thenThrow(new ServiceException(HttpStatus.NOT_FOUND_404, "No Player found!"));

        Player incomingPlayerBase = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(false)
                .withId(existingPlayerId)
                .withMetadata()
                .withAbility()
                .withRoles()
                .withAttributes()
                .build();
        Player incomingPlayer = PlayerDataProvider.ModifiedPlayerBuilder.builder()
                .from(incomingPlayerBase)
                .withUpdatedNameInMetadata(updatedPlayerName)
                .withUpdatedRoleName(updatedRoleName)
                .withUpdatedAttributeValue(updatedAttributeName, UPDATED_PLAYER_SPRINT_SPEED)
                .build();

        // execute
        assertThrows(ServiceException.class,
                () -> playerResource.updatePlayer(userPrincipal, existingPlayerId, incomingPlayer));

        // assert
        verify(playerService).getPlayer(any());
        verify(clubService, never()).doesClubBelongToUser(any(), any());
        verify(playerService, never()).updatePlayer(any(), any(), any());
    }

    /**
     * given a valid player entity in the request, tests that if the user does not have access to the player, no player
     * data is updated and a service exception is thrown instead
     */
    @Test
    public void updatePlayerWhenPlayerDoesNotBelongToUser() {
        // setup
        String updatedPlayerName = "updated name";
        String updatedAttributeName = "sprint speed";
        String updatedRoleName = "updated player role";
        UUID existingPlayerId = UUID.randomUUID();

        Player existingPlayer = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(true)
                .withId(existingPlayerId)
                .withMetadata()
                .withAbility()
                .withRoles()
                .withAttributes()
                .build();
        when(playerService.getPlayer(any())).thenReturn(existingPlayer);

        when(clubService.doesClubBelongToUser(eq(existingPlayer.getClubId()), eq(userPrincipal.getId())))
                .thenReturn(false);

        Player incomingPlayerBase = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(false)
                .withId(existingPlayerId)
                .withMetadata()
                .withAbility()
                .withRoles()
                .withAttributes()
                .build();
        Player incomingPlayer = PlayerDataProvider.ModifiedPlayerBuilder.builder()
                .from(incomingPlayerBase)
                .withUpdatedNameInMetadata(updatedPlayerName)
                .withUpdatedRoleName(updatedRoleName)
                .withUpdatedAttributeValue(updatedAttributeName, UPDATED_PLAYER_SPRINT_SPEED)
                .build();

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> playerResource.updatePlayer(userPrincipal, existingPlayerId, incomingPlayer));

        // assert
        verify(playerService).getPlayer(any());
        verify(clubService).doesClubBelongToUser(any(), any());
        verify(playerService, never()).updatePlayer(any(), any(), any());
        assertEquals(HttpStatus.FORBIDDEN_403, serviceException.getResponseStatus());
    }

    /**
     * given that the request contains a player entity whose ID does not match the existing player's ID, tests that
     * the associated player data is not updated and a service exception is thrown instead
     */
    @Test
    public void updatePlayerWhenIncomingPlayerIdDoesNotMatchExisting() {
        // setup
        UUID existingPlayerId = UUID.randomUUID();
        UUID incomingPlayerId = UUID.randomUUID();
        Player incomingPlayer = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(false)
                .withId(incomingPlayerId)
                .withMetadata()
                .withRoles()
                .withAttributes()
                .build();

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> playerResource.updatePlayer(userPrincipal, existingPlayerId, incomingPlayer));

        // assert
        verify(playerService, never()).getPlayer(any());
        verify(clubService, never()).doesClubBelongToUser(any(), any());
        verify(playerService, never()).updatePlayer(any(), any(), any());
        assertEquals(HttpStatus.CONFLICT_409, serviceException.getResponseStatus());
    }

    /**
     * given a valid player id, tests that the player data is removed and a 204 No Content response is returned
     */
    @Test
    public void deletePlayerRemovesPlayerData() {
        // setup
        UUID playerId = UUID.randomUUID();

        // execute
        Response playerResponse = playerResource.deletePlayer(userPrincipal, playerId);

        // assert
        verify(playerService).deletePlayer(eq(playerId), any());
        assertEquals(HttpStatus.NO_CONTENT_204, playerResponse.getStatus());
    }
}