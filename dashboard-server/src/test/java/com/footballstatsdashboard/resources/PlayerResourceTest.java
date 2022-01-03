package com.footballstatsdashboard.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.footballstatsdashboard.api.model.ImmutablePlayer;
import com.footballstatsdashboard.api.model.ImmutableUser;
import com.footballstatsdashboard.api.model.Player;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.api.model.club.Club;
import com.footballstatsdashboard.api.model.club.ImmutableClub;
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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
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
    private static final List<String> PLAYER_ATTRIBUTE_CATEGORIES = ImmutableList.of("Technical", "Physical", "Mental");
    private static final List<String> PLAYER_ATTRIBUTE_GROUPS = ImmutableList.of("Attacking", "Aerial", "Vision",
            "Defending", "Speed");
    private static final ObjectMapper OBJECT_MAPPER = Jackson.newObjectMapper().copy();

    private PlayerResource playerResource;
    private User userPrincipal;

    @Mock
    private CouchbaseDAO<ResourceKey> couchbaseDAO;

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
    public void getPlayerFetchesPlayerFromCouchbase() {
        // setup
        UUID playerId = UUID.randomUUID();
        Player playerFromCouchbase = getPlayerDataStub(playerId, true, true, false);
        when(couchbaseDAO.getDocument(any(), any())).thenReturn(playerFromCouchbase);

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
    public void getPlayerWhenPlayerNotFoundInCouchbase() {
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
    public void createPlayerPersistsPlayerInCouchbase() {
        // setup
        Player incomingPlayer = getPlayerDataStub(null, true, true, false);
        ArgumentCaptor<Player> newPlayerCaptor = ArgumentCaptor.forClass(Player.class);
        ArgumentCaptor<ResourceKey> clubResourceKeyCaptor = ArgumentCaptor.forClass(ResourceKey.class);
        Club existingClub = ImmutableClub.builder()
                .name("fake club name")
                .expenditure(new BigDecimal("1000"))
                .income(new BigDecimal("2000"))
                .transferBudget(new BigDecimal("500"))
                .wageBudget(new BigDecimal("200"))
                .build();
        when(couchbaseDAO.getDocument(any(), any())).thenReturn(existingClub);

        // execute
        Response playerResponse = playerResource.createPlayer(userPrincipal, incomingPlayer, uriInfo);

        // assert
        verify(couchbaseDAO).getDocument(clubResourceKeyCaptor.capture(), eq(Club.class));
        ResourceKey capturedClubResourceKey = clubResourceKeyCaptor.getValue();
        assertEquals(incomingPlayer.getClubId(), capturedClubResourceKey.getResourceId());

        verify(couchbaseDAO).insertDocument(any(), newPlayerCaptor.capture());
        Player newPlayer = newPlayerCaptor.getValue();
        assertEquals(existingClub.getName(), newPlayer.getMetadata().getClub());
        assertNotNull(newPlayer.getMetadata().getClubLogo());

        // TODO: add assertions to verify country logo property is correctly set on the basis of the country property
        //  set on the incoming player
        assertNotNull(newPlayer.getMetadata().getCountryLogo());

        newPlayer.getAttributes().forEach(attribute -> {
            assertTrue(attribute.getCategory() != null
                    && PLAYER_ATTRIBUTE_CATEGORIES.contains(attribute.getCategory()));
            assertTrue(attribute.getGroup() != null
                    && PLAYER_ATTRIBUTE_GROUPS.contains(attribute.getGroup()));
            assertNotNull(attribute.getGroup());
            assertEquals(1, attribute.getHistory().size());
            assertEquals(attribute.getValue(), attribute.getHistory().get(0));
        });

        // assertions for general house-keeping fields
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
    public void createPlayerWhenPlayerRolesNotProvided() {
        // setup
        Player incomingPlayer = getPlayerDataStub(null, false, true, false);

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
    public void createPlayerWhenPlayerAttributesNotProvided() {
        // setup
        Player incomingPlayer = getPlayerDataStub(null, true, false, false);

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
    public void updatePlayerUpdatesPlayerInCouchbase() {
        // setup
        UUID existingPlayerId = UUID.randomUUID();
        Player existingPlayerInCouchbase = getPlayerDataStub(existingPlayerId, true, true, true);
        Player incomingPlayerBase = getPlayerDataStub(existingPlayerId, true, true, false);
        when(couchbaseDAO.getDocument(any(), any())).thenReturn(existingPlayerInCouchbase);

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

        ArgumentCaptor<Player> updatedPlayerCaptor = ArgumentCaptor.forClass(Player.class);
        ArgumentCaptor<ResourceKey> resourceKeyCaptor = ArgumentCaptor.forClass(ResourceKey.class);

        // execute
        Response playerResponse = playerResource.updatePlayer(userPrincipal, existingPlayerId, incomingPlayer);

        // assert
        verify(couchbaseDAO).getDocument(resourceKeyCaptor.capture(), any());
        ResourceKey capturedResourceKey = resourceKeyCaptor.getValue();
        assertEquals(existingPlayerId, capturedResourceKey.getResourceId());

        verify(couchbaseDAO).updateDocument(eq(capturedResourceKey), updatedPlayerCaptor.capture());
        Player updatedPlayer = updatedPlayerCaptor.getValue();
        updatedPlayer.getAttributes().forEach(attribute -> {
            Attribute existingPlayerAttribute = existingPlayerInCouchbase.getAttributes().stream()
                    .filter(existingAttribute -> existingAttribute.getName().equals(attribute.getName()))
                    .findFirst().orElse(null);
            assertNotNull(existingPlayerAttribute);
            assertTrue(attribute.getCategory() != null
                    && PLAYER_ATTRIBUTE_CATEGORIES.contains(attribute.getCategory()));
            assertTrue(attribute.getGroup() != null
                    && PLAYER_ATTRIBUTE_GROUPS.contains(attribute.getGroup()));
            assertEquals(existingPlayerAttribute.getHistory().size() + 1, attribute.getHistory().size());
            assertEquals(ImmutableList.of(existingPlayerAttribute.getValue(), attribute.getValue()),
                    attribute.getHistory());
        });
        assertNotEquals(existingPlayerInCouchbase.getLastModifiedDate(), updatedPlayer.getLastModifiedDate());
        assertEquals(userPrincipal.getEmail(), updatedPlayer.getCreatedBy());

        assertEquals(HttpStatus.OK_200, playerResponse.getStatus());
        assertNotNull(playerResponse.getEntity());

        Player playerInResponse = OBJECT_MAPPER.convertValue(playerResponse.getEntity(), Player.class);
        assertEquals(incomingPlayer.getId(), playerInResponse.getId());
        assertEquals(incomingPlayer.getMetadata(), playerInResponse.getMetadata());
        assertEquals(incomingPlayer.getRoles(), playerInResponse.getRoles());
        assertEquals(incomingPlayer.getAbility(), playerInResponse.getAbility());
        assertEquals(userPrincipal.getEmail(), playerInResponse.getCreatedBy());
    }

    /**
     * given that the request contains a player entity whose ID does not match the existing player's ID, tests that
     * the invalid entity is not upserted in couchbase and a server error response is returned
     */
    @Test
    public void updatePlayerWhenIncomingPlayerIdDoesNotMatchExisting() {
        // setup
        UUID existingPlayerId = UUID.randomUUID();
        Player existingPlayerInCouchbase = getPlayerDataStub(existingPlayerId, true, true, true);
        when(couchbaseDAO.getDocument(any(), any())).thenReturn(existingPlayerInCouchbase);

        UUID incomingPlayerId = UUID.randomUUID();
        Player incomingPlayer = ImmutablePlayer.builder()
                .from(existingPlayerInCouchbase)
                .id(incomingPlayerId)
                .build();

        // execute
        Response playerResponse = playerResource.updatePlayer(userPrincipal, existingPlayerId, incomingPlayer);

        // assert
        verify(couchbaseDAO).getDocument(any(), any());
        verify(couchbaseDAO, never()).updateDocument(any(), any());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR_500, playerResponse.getStatus());
        assertTrue(playerResponse.getEntity().toString().contains(incomingPlayerId.toString()));
    }

    /**
     * given a valid player id, removes the player entity from couchbase
     */
    @Test
    public void deletePlayerRemovesPlayerFromCouchbase() {
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
    public void deletePlayerWhenPlayerNotFound() {
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