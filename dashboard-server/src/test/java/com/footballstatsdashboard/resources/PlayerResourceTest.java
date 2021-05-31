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
import com.footballstatsdashboard.db.CouchbaseDAO;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for player resource
 */
public class PlayerResourceTest {

    private static final String URI_PATH = "/players";
    private PlayerResource playerResource;
    private User userPrincipal;
    private static final ObjectMapper OBJECT_MAPPER = Jackson.newObjectMapper().copy();

    @Mock
    CouchbaseDAO<ResourceKey> couchbaseDAO;

    @Mock
    UriInfo uriInfo;

    /**
     * set up test data before each test case is run
     */
    @Before
    public void initialize() {
        MockitoAnnotations.openMocks(this);

        UriBuilder uriBuilder = UriBuilder.fromPath(URI_PATH);
        when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);

        playerResource = new PlayerResource(couchbaseDAO);
        userPrincipal = ImmutableUser.builder()
                .email("fake email")
                // other details are not required for the purposes of this test so using empty strings
                .password("")
                .firstName("")
                .lastName("")
                .build();
    }

    /**
     * given a valid player id, tests that the player entity is successfully fetched from couchbase server and
     * returned in the response
     */
    @Test
    public void getPlayer_fetchesPlayerFromCouchbase() {
        // setup
        UUID playerId = UUID.randomUUID();
        Player playerFromCouchbase = getPlayerDataStub(playerId, true, true);
        when(couchbaseDAO.getDocument(any(), any()))
                .thenReturn(Pair.of(playerFromCouchbase, 0L));

        // execute
        Response playerResponse = playerResource.getPlayer(playerId);

        // assert
        verify(couchbaseDAO).getDocument(any(), any());
        assertEquals(HttpStatus.OK_200, playerResponse.getStatus());
        assertNotNull(playerResponse.getEntity());

        Player playerFromResponse = OBJECT_MAPPER.convertValue(playerResponse.getEntity(), Player.class);
        assertEquals(playerId, playerFromResponse.getId());
    }

    /**
     * given a runtime exception is thrown by couchbase DAO when player entity is not found, verifies that the same
     * exception is thrown by `getPlayer` resource method as well
     */
    @Test(expected = RuntimeException.class)
    public void getPlayer_playerNotFoundInCouchbase() {
        // setup
        UUID invalidPlayerId = UUID.randomUUID();
        when(couchbaseDAO.getDocument(any(), any()))
                .thenThrow(new RuntimeException("Unable to find document with Id: " + invalidPlayerId));

        // execute
        playerResource.getPlayer(invalidPlayerId);

        // assert
        verify(couchbaseDAO).getDocument(any(), any());
    }

    /**
     * given a valid player entity in the request, tests that the internal fields are set correctly on the entity and
     * persisted in couchbase
     */
    @Test
    public void createPlayer_persistsPlayerInCouchbase() {
        // setup
        Player incomingPlayer = getPlayerDataStub(null, true, true);
        ArgumentCaptor<Player> newPlayerCaptor = ArgumentCaptor.forClass(Player.class);

        // execute
        Response playerResponse = playerResource.createPlayer(userPrincipal, incomingPlayer, uriInfo);

        // assert
        verify(couchbaseDAO).insertDocument(any(), newPlayerCaptor.capture());
        Player newPlayer = newPlayerCaptor.getValue();
        assertNotNull(newPlayer.getCreatedDate());
        assertNotNull(newPlayer.getLastModifiedDate());
        assertEquals(userPrincipal.getEmail(), newPlayer.getCreatedBy());

        assertEquals(HttpStatus.CREATED_201, playerResponse.getStatus());
        assertNotNull(playerResponse.getEntity());
        // a playerId is set on the player instance created despite not setting one explicitly due to the way the
        // interface has been set up
        assertEquals(URI_PATH + "/" + incomingPlayer.getId().toString(), playerResponse.getLocation().getPath());
    }

    /**
     * given that the request contains a player entity without player roles, tests that the entity is never persisted
     * to couchbase and a 422 response status is returned
     */
    @Test
    public void createPlayer_playerRolesNotProvided() {
        // setup
        Player incomingPlayer = getPlayerDataStub(null, false, true);

        // execute
        Response playerResponse = playerResource.createPlayer(userPrincipal, incomingPlayer, uriInfo);

        // assert
        verify(couchbaseDAO, never()).insertDocument(any(), any());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422, playerResponse.getStatus());
        assertNotNull(playerResponse.getEntity());
        assertEquals(incomingPlayer, playerResponse.getEntity());
    }

    /**
     * given that the request contains a player entity without player attributes, tests that the entity is never
     * persisted to couchbase and a 422 response status is returned
     */
    @Test
    public void createPlayer_playerAttributesNotProvided() {
        // setup
        Player incomingPlayer = getPlayerDataStub(null, true, false);

        // execute
        Response playerResponse = playerResource.createPlayer(userPrincipal, incomingPlayer, uriInfo);

        // assert
        verify(couchbaseDAO, never()).insertDocument(any(), any());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422, playerResponse.getStatus());
        assertNotNull(playerResponse.getEntity());
        assertEquals(incomingPlayer, playerResponse.getEntity());
    }

    /**
     * given a valid player entity in the request, tests that an updated player entity with updated internal fields
     * is upserted in couchbase
     */
    @Test
    public void updatePlayer_updatesPlayerInCouchbase() {
        // setup
        UUID existingPlayerId = UUID.randomUUID();
        Long existingPlayerCAS = 123L;
        Player existingPlayerInCouchbase = getPlayerDataStub(existingPlayerId, true, true);
        when(couchbaseDAO.getDocument(any(), any())).thenReturn(Pair.of(existingPlayerInCouchbase, existingPlayerCAS));

        Metadata updatedMetadata = ImmutableMetadata.builder()
                .from(existingPlayerInCouchbase.getMetadata())
                .name("Updated Name")
                .build();
        Ability updatedAbility = ImmutableAbility.builder()
                .from(existingPlayerInCouchbase.getAbility())
                .current(25)
                .build();
        Role updatedRole = ImmutableRole.builder()
                .from(existingPlayerInCouchbase.getRoles().get(0))
                .name("updated playerRole")
                .build();

        Attribute updatedAttribute = ImmutableAttribute.builder()
                .from(existingPlayerInCouchbase.getAttributes().get(0))
                .history(ImmutableList.of(87))
                .build();
        Player incomingPlayer = ImmutablePlayer.builder()
                .from(existingPlayerInCouchbase)
                .metadata(updatedMetadata)
                .ability(updatedAbility)
                .roles(ImmutableList.of(updatedRole))
                .attributes(ImmutableList.of(updatedAttribute))
                .build();

        ArgumentCaptor<Player> updatedPlayerCaptor = ArgumentCaptor.forClass(Player.class);
        ArgumentCaptor<Long> existingPlayerCASCaptor = ArgumentCaptor.forClass(Long.class);

        // execute
        Response playerResponse = playerResource.updatePlayer(userPrincipal, existingPlayerId, incomingPlayer);

        // assert
        verify(couchbaseDAO).getDocument(any(), any());

        verify(couchbaseDAO).updateDocument(any(), updatedPlayerCaptor.capture(), existingPlayerCASCaptor.capture());
        Player updatedPlayer = updatedPlayerCaptor.getValue();
        assertNotNull(updatedPlayer.getLastModifiedDate());
        assertNotNull(updatedPlayer.getCreatedBy());

        assertEquals(existingPlayerCAS, existingPlayerCASCaptor.getValue());

        assertEquals(HttpStatus.OK_200, playerResponse.getStatus());
        assertNotNull(playerResponse.getEntity());

        Player playerInResponse = OBJECT_MAPPER.convertValue(playerResponse.getEntity(), Player.class);
        assertEquals(incomingPlayer.getId(), playerInResponse.getId());
        assertEquals(incomingPlayer.getMetadata(), playerInResponse.getMetadata());
        assertEquals(incomingPlayer.getRoles(), playerInResponse.getRoles());
        assertEquals(incomingPlayer.getAbility(), playerInResponse.getAbility());
        assertEquals(incomingPlayer.getAttributes(), playerInResponse.getAttributes());
        assertEquals(userPrincipal.getEmail(), playerInResponse.getCreatedBy());
    }

    /**
     * given that the request contains a player entity whose ID does not match the existing player's ID, tests that
     * the invalid entity is not upserted in couchbase and a server error response is returned
     */
    @Test
    public void updatePlayer_incomingPlayerIdDoesNotMatchExisting() {
        // setup
        UUID existingPlayerId = UUID.randomUUID();
        Long existingPlayerCAS = 123L;
        Player existingPlayerInCouchbase = getPlayerDataStub(existingPlayerId, true, true);
        when(couchbaseDAO.getDocument(any(), any())).thenReturn(Pair.of(existingPlayerInCouchbase, existingPlayerCAS));

        UUID incomingPlayerId = UUID.randomUUID();
        Player incomingPlayer = ImmutablePlayer.builder()
                .from(existingPlayerInCouchbase)
                .id(incomingPlayerId)
                .build();

        // execute
        Response playerResponse = playerResource.updatePlayer(userPrincipal, existingPlayerId, incomingPlayer);

        // assert
        verify(couchbaseDAO).getDocument(any(), any());
        verify(couchbaseDAO, never()).updateDocument(any(), any(), anyLong());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR_500, playerResponse.getStatus());
        assertTrue(playerResponse.getEntity().toString().contains(incomingPlayerId.toString()));
    }

    /**
     * given a valid player id, removes the player entity from couchbase
     */
    @Test
    public void deletePlayer_removesPlayerFromCouchbase() {
        // setup
        UUID playerId = UUID.randomUUID();
        ArgumentCaptor<ResourceKey> resourceKeyCaptor = ArgumentCaptor.forClass(ResourceKey.class);

        // execute
        Response playerResponse = playerResource.deletePlayer(playerId);

        // assert
        verify(couchbaseDAO).deleteDocument(resourceKeyCaptor.capture());
        assertEquals(playerId, resourceKeyCaptor.getValue().getResourceId());

        assertEquals(HttpStatus.NO_CONTENT_204, playerResponse.getStatus());
    }

    /**
     * given that the couchbase DAO throws a RuntimeException when it cannot find the player entity to remove, the
     * same exception is propagated and thrown by the resource method as well
     */
    @Test(expected = RuntimeException.class)
    public void deletePlayer_playerNotFound() {
        // setup
        UUID playerId = UUID.randomUUID();
        ArgumentCaptor<ResourceKey> resourceKeyCaptor = ArgumentCaptor.forClass(ResourceKey.class);
        doThrow(new RuntimeException("player not found")).when(couchbaseDAO).deleteDocument(any());

        // execute
        playerResource.deletePlayer(playerId);

        // assert
        verify(couchbaseDAO).deleteDocument(resourceKeyCaptor.capture());
        assertEquals(playerId, resourceKeyCaptor.getValue().getResourceId());
    }

    private Player getPlayerDataStub(UUID playerId, boolean usePlayerRoles, boolean usePlayerAttributes) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
        Metadata playerMetadata = ImmutableMetadata.builder()
                .dateOfBirth(LocalDate.parse("16/08/2006", formatter))
                .build();
        Ability playerAbility = ImmutableAbility.builder()
                .current(19)
                .build();

        ImmutablePlayer.Builder playerBuilder = ImmutablePlayer.builder()
                .metadata(playerMetadata)
                .ability(playerAbility);

        if (playerId != null) {
            playerBuilder.id(playerId);
        }

        if (usePlayerRoles) {
            Role playerRole = ImmutableRole.builder()
                    .name("playerRole")
                    .build();
            playerBuilder.roles(ImmutableList.of(playerRole));
        }

        if (usePlayerAttributes) {
            Attribute playerAttribute = ImmutableAttribute.builder()
                    .name("Sprint Speed")
                    .value(85)
                    .category("Technical")
                    .group("Speed")
                    .build();

            playerBuilder.attributes(ImmutableList.of(playerAttribute));
        }
        return playerBuilder.build();
    }
}
