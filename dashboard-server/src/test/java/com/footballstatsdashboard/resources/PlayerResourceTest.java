package com.footballstatsdashboard.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.footballstatsdashboard.PlayerDataProvider;
import com.footballstatsdashboard.api.model.ImmutableUser;
import com.footballstatsdashboard.api.model.Player;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.api.model.club.Club;
import com.footballstatsdashboard.api.model.club.ImmutableClub;
import com.footballstatsdashboard.db.CouchbaseDAO;
import com.footballstatsdashboard.db.key.ResourceKey;
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
import java.math.BigDecimal;
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
    private static final int UPDATED_PLAYER_ABILITY = 25;
    private static final int UPDATED_PLAYER_SPRINT_SPEED = 87;
    private static final ObjectMapper OBJECT_MAPPER = Jackson.newObjectMapper().copy();

    private PlayerResource playerResource;
    private User userPrincipal;

    @Mock
    private PlayerService playerService;

    @Mock
    private CouchbaseDAO<ResourceKey> clubCouchbase;

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

        playerResource = new PlayerResource(playerService, clubCouchbase);
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
        Player playerFromCouchbase = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(true)
                .withId(playerId)
                .withMetadata()
                .withAbility()
                .withRoles()
                .withAttributes()
                .build();
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
        Club existingClub = ImmutableClub.builder()
                .name("fake club name")
                .income(new BigDecimal("100"))
                .expenditure(new BigDecimal("100"))
                .wageBudget(new BigDecimal("100"))
                .transferBudget(new BigDecimal("100"))
                .build();
        when(clubCouchbase.getDocument(any(), any())).thenReturn(existingClub);
        when(playerService.createPlayer(any(), any(), anyString())).thenReturn(createdPlayer);

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
     * given that the request contains a player entity without player roles, tests that no data is persisted and a 422
     * response status is returned
     */
    @Test
    public void createPlayerWhenPlayerRolesNotProvided() {
        // setup
        Player incomingPlayer = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(false)
                .withMetadata()
                .withAbility()
                .withAttributes()
                .build();

        // execute
        Response playerResponse = playerResource.createPlayer(userPrincipal, incomingPlayer, uriInfo);

        // assert
        verify(clubCouchbase, never()).getDocument(any(), any());
        verify(playerService, never()).createPlayer(any(), any(), anyString());
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
        Player incomingPlayer = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(false)
                .withMetadata()
                .withRoles()
                .build();

        // execute
        Response playerResponse = playerResource.createPlayer(userPrincipal, incomingPlayer, uriInfo);

        // assert
        verify(clubCouchbase, never()).getDocument(any(), any());
        verify(playerService, never()).createPlayer(any(), any(), anyString());
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
        String updatedPlayerName = "updated name";
        String updatedAttributeName = "sprint speed";
        String updatedRoleName = "updated player role";

        UUID existingPlayerId = UUID.randomUUID();
        Player existingPlayerInCouchbase = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(true)
                .withId(existingPlayerId)
                .withMetadata()
                .withAbility()
                .withRoles()
                .withAttributes()
                .build();
        when(playerService.getPlayer(any())).thenReturn(existingPlayerInCouchbase);

        Player updatedPlayerInCouchbase = PlayerDataProvider.ModifiedPlayerBuilder.builder()
                .from(existingPlayerInCouchbase)
                .withUpdatedNameInMetadata(updatedPlayerName)
                .withUpdatedCurrentAbility(UPDATED_PLAYER_ABILITY)
                .withUpdatedRoleName(updatedRoleName)
                .withUpdatedAttributeValue(updatedAttributeName, UPDATED_PLAYER_SPRINT_SPEED)
                .build();
        when(playerService.updatePlayer(any(), any(), any())).thenReturn(updatedPlayerInCouchbase);

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
        verify(playerService).updatePlayer(any(), any(), eq(existingPlayerId));

        assertEquals(HttpStatus.OK_200, playerResponse.getStatus());
        assertNotNull(playerResponse.getEntity());

        Player playerInResponse = OBJECT_MAPPER.convertValue(playerResponse.getEntity(), Player.class);
        assertEquals(updatedPlayerInCouchbase.getId(), playerInResponse.getId());
        assertEquals(updatedPlayerInCouchbase.getMetadata(), playerInResponse.getMetadata());
        assertEquals(updatedPlayerInCouchbase.getRoles(), playerInResponse.getRoles());
        assertEquals(updatedPlayerInCouchbase.getAbility(), playerInResponse.getAbility());
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
        Player existingPlayerInCouchbase = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(true)
                .withId(existingPlayerId)
                .withMetadata()
                .withAbility()
                .withRoles()
                .withAttributes()
                .build();
        when(playerService.getPlayer(eq(existingPlayerId))).thenReturn(existingPlayerInCouchbase);

        UUID incomingPlayerId = UUID.randomUUID();
        Player incomingPlayer = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(false)
                .withId(incomingPlayerId)
                .withMetadata()
                .withRoles()
                .withAttributes()
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
}