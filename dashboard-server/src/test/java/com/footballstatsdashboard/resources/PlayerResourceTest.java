package com.footballstatsdashboard.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.footballstatsdashboard.api.model.ImmutablePlayer;
import com.footballstatsdashboard.api.model.ImmutableUser;
import com.footballstatsdashboard.api.model.Player;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.api.model.player.Ability;
import com.footballstatsdashboard.api.model.player.Attribute;
import com.footballstatsdashboard.api.model.player.ImmutableAbility;
import com.footballstatsdashboard.api.model.player.ImmutableAttribute;
import com.footballstatsdashboard.api.model.player.ImmutableMetadata;
import com.footballstatsdashboard.api.model.player.ImmutableRole;
import com.footballstatsdashboard.api.model.player.Metadata;
import com.footballstatsdashboard.api.model.player.Role;
import com.footballstatsdashboard.services.PlayerService;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
    private static final int PLAYER_AGE = 27;
    private static final int CURRENT_PLAYER_ABILITY = 19;
    private static final int CURRENT_PLAYER_SPRINT_SPEED = 85;
    private static final int UPDATED_PLAYER_ABILITY = 25;
    private static final int UPDATED_PLAYER_SPRINT_SPEED = 87;
    private static final ObjectMapper OBJECT_MAPPER = Jackson.newObjectMapper().copy();

    private PlayerResource playerResource;
    private User userPrincipal;

    @Mock
    private PlayerService playerService;

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

        playerResource = new PlayerResource(playerService);
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
        Player playerFromCouchbase = getPlayerDataStub(playerId, true, true, false);
        when(playerService.getPlayer(eq(playerId))).thenReturn(playerFromCouchbase);

        // execute
        Response playerResponse = playerResource.getPlayer(playerId);

        // assert
        verify(playerService).getPlayer(any());
        assertEquals(HttpStatus.OK_200, playerResponse.getStatus());
        assertNotNull(playerResponse.getEntity());

        Player playerFromResponse = OBJECT_MAPPER.convertValue(playerResponse.getEntity(), Player.class);
        assertEquals(playerId, playerFromResponse.getId());
    }

    // TODO: 1/3/2022 test that the runtime exception thrown when a couchbase document is not found results in a 404

    /**
     * given a valid player entity in the request, tests that the player data is successfully persisted
     */
    @Test
    public void createPlayerPersistsPlayerData() {
        // setup
        Player incomingPlayer = getPlayerDataStub(null, true, true, false);
        Player createdPlayer = getPlayerDataStub(incomingPlayer.getId(), true, true, true);
        when(playerService.createPlayer(any(), anyString())).thenReturn(createdPlayer);

        // execute
        Response playerResponse = playerResource.createPlayer(userPrincipal, incomingPlayer, uriInfo);

        // assert
        verify(playerService).createPlayer(eq(incomingPlayer), eq(userPrincipal.getEmail()));
        assertEquals(HttpStatus.CREATED_201, playerResponse.getStatus());
        assertNotNull(playerResponse.getEntity());
        // a playerId is set on the player instance created despite not setting one explicitly due to the way the
        // interface has been set up
        assertEquals(URI_PATH + "/" + incomingPlayer.getId().toString(), playerResponse.getLocation().getPath());
    }

    /**
     * given that the request contains a player entity without player roles, tests that no data is persisted and a 422
     * response status is returned
     */
    @Test
    public void createPlayerWhenPlayerRolesNotProvided() {
        // setup
        Player incomingPlayer = getPlayerDataStub(null, false, true, false);

        // execute
        Response playerResponse = playerResource.createPlayer(userPrincipal, incomingPlayer, uriInfo);

        // assert
        verify(playerService, never()).createPlayer(any(), anyString());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422, playerResponse.getStatus());
        assertNotNull(playerResponse.getEntity());
        assertEquals(incomingPlayer, playerResponse.getEntity());
    }

    /**
     * given that the request contains a player entity without player attributes, tests that no data is persisted and a
     * 422 response status is returned
     */
    @Test
    public void createPlayerWhenPlayerAttributesNotProvided() {
        // setup
        Player incomingPlayer = getPlayerDataStub(null, true, false, false);

        // execute
        Response playerResponse = playerResource.createPlayer(userPrincipal, incomingPlayer, uriInfo);

        // assert
        verify(playerService, never()).createPlayer(any(), anyString());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422, playerResponse.getStatus());
        assertNotNull(playerResponse.getEntity());
        assertEquals(incomingPlayer, playerResponse.getEntity());
    }

    /**
     * given a valid player entity in the request, tests that the corresponding player data is updated
     */
    @Test
    public void updatePlayerUpdatesPlayerData() {
        // setup
        UUID existingPlayerId = UUID.randomUUID();
        Player existingPlayerInCouchbase = getPlayerDataStub(existingPlayerId, true, true, true);
        when(playerService.getPlayer(any())).thenReturn(existingPlayerInCouchbase);
        // TODO: 1/3/2022 replace this with proper test data creator method
        Player updatedPlayerInCouchbase = getPlayerDataStub(existingPlayerId, true, true, true);
        when(playerService.updatePlayer(any(), any(), any())).thenReturn(updatedPlayerInCouchbase);

        Player incomingPlayerBase = getPlayerDataStub(existingPlayerId, true, true, false);
        Metadata updatedMetadata = ImmutableMetadata.builder()
                .from(incomingPlayerBase.getMetadata())
                .name("Updated Name")
                .build();
        Ability updatedAbility = ImmutableAbility.builder()
                .from(incomingPlayerBase.getAbility())
                .current(UPDATED_PLAYER_ABILITY)
                .build();
        Role updatedRole = ImmutableRole.builder()
                .from(incomingPlayerBase.getRoles().get(0))
                .name("updated playerRole")
                .build();
        Attribute updatedAttribute = ImmutableAttribute.builder()
                .from(incomingPlayerBase.getAttributes().get(0))
                .value(UPDATED_PLAYER_SPRINT_SPEED)
                .build();

        Player incomingPlayer = ImmutablePlayer.builder()
                .from(incomingPlayerBase)
                .metadata(updatedMetadata)
                .ability(updatedAbility)
                .roles(ImmutableList.of(updatedRole))
                .attributes(ImmutableList.of(updatedAttribute))
                .build();

        // execute
        Response playerResponse = playerResource.updatePlayer(userPrincipal, existingPlayerId, incomingPlayer);

        // assert
        verify(playerService).getPlayer(eq(existingPlayerId));
        verify(playerService).updatePlayer(any(), any(), eq(existingPlayerId));

        assertEquals(HttpStatus.OK_200, playerResponse.getStatus());
        assertNotNull(playerResponse.getEntity());

        Player playerInResponse = OBJECT_MAPPER.convertValue(playerResponse.getEntity(), Player.class);
        // TODO: 1/3/2022 uncomment these when the TODO item above is resolved
//        assertEquals(incomingPlayer.getId(), playerInResponse.getId());
//        assertEquals(incomingPlayer.getMetadata(), playerInResponse.getMetadata());
//        assertEquals(incomingPlayer.getRoles(), playerInResponse.getRoles());
//        assertEquals(incomingPlayer.getAbility(), playerInResponse.getAbility());
        assertEquals(userPrincipal.getEmail(), playerInResponse.getCreatedBy());
    }

    /**
     * given that the request contains a player entity whose ID does not match the existing player's ID, tests that
     * the associated player data is not updated and a server error response is returned
     */
    @Test
    public void updatePlayerWhenIncomingPlayerIdDoesNotMatchExisting() {
        // setup
        UUID existingPlayerId = UUID.randomUUID();
        Player existingPlayerInCouchbase = getPlayerDataStub(existingPlayerId, true, true, true);
        when(playerService.getPlayer(eq(existingPlayerId))).thenReturn(existingPlayerInCouchbase);

        UUID incomingPlayerId = UUID.randomUUID();
        Player incomingPlayerBase = getPlayerDataStub(existingPlayerId, true, true, false);
        Player incomingPlayer = ImmutablePlayer.builder()
                .from(incomingPlayerBase)
                .id(incomingPlayerId)
                .build();

        // execute
        Response playerResponse = playerResource.updatePlayer(userPrincipal, existingPlayerId, incomingPlayer);

        // assert
        verify(playerService).getPlayer(any());
        verify(playerService, never()).updatePlayer(any(), any(), any());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR_500, playerResponse.getStatus());
        assertTrue(playerResponse.getEntity().toString().contains(incomingPlayerId.toString()));
    }

    /**
     * given a valid player id, removes the player data and a 204 No Content response is returned
     */
    @Test
    public void deletePlayerRemovesPlayerData() {
        // setup
        UUID playerId = UUID.randomUUID();

        // execute
        Response playerResponse = playerResource.deletePlayer(playerId);

        // assert
        verify(playerService).deletePlayer(eq(playerId));
        assertEquals(HttpStatus.NO_CONTENT_204, playerResponse.getStatus());
    }

    // TODO: 1/3/2022 test that the runtime exception thrown when a couchbase document is not found results in a 404

    private Player getPlayerDataStub(UUID playerId, boolean usePlayerRoles, boolean usePlayerAttributes,
                                     boolean isExistingPlayer) {
        Metadata playerMetadata = ImmutableMetadata.builder()
                .name("fake player name")
                .country("fake country name")
                .age(PLAYER_AGE)
                .club(isExistingPlayer ? "fake club name" : null)
                .clubLogo(isExistingPlayer ? "fake club logo" : null)
                .countryLogo(isExistingPlayer ? "fake country logo" : null)
                .build();
        Ability playerAbility = ImmutableAbility.builder()
                .current(CURRENT_PLAYER_ABILITY)
                .build();

        ImmutablePlayer.Builder playerBuilder = ImmutablePlayer.builder()
                .metadata(playerMetadata)
                .ability(playerAbility);

        if (playerId != null) {
            playerBuilder.id(playerId);
        }

        if (isExistingPlayer) {
            Instant currentInstant = Instant.now();
            Instant olderInstant = currentInstant.minus(1, ChronoUnit.DAYS);
            playerBuilder.lastModifiedDate(LocalDate.ofInstant(olderInstant, ZoneId.systemDefault()));
        }

        if (usePlayerRoles) {
            Role playerRole = ImmutableRole.builder()
                    .name("playerRole")
                    .build();
            playerBuilder.roles(ImmutableList.of(playerRole));
        }

        if (usePlayerAttributes) {
            Attribute playerAttribute = ImmutableAttribute.builder()
                    .name("sprint speed")
                    .value(CURRENT_PLAYER_SPRINT_SPEED)
                    .category(isExistingPlayer ? "Technical" : null)
                    .group(isExistingPlayer ? "Speed" : null)
                    .history(isExistingPlayer ? ImmutableList.of(CURRENT_PLAYER_SPRINT_SPEED) : ImmutableList.of())
                    .build();

            playerBuilder.attributes(ImmutableList.of(playerAttribute));
        }
        return playerBuilder.clubId(UUID.randomUUID()).build();
    }
}