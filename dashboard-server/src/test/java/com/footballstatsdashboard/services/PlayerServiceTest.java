package com.footballstatsdashboard.services;

import com.footballstatsdashboard.api.model.ImmutablePlayer;
import com.footballstatsdashboard.api.model.Player;
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
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PlayerServiceTest {
    private static final int PLAYER_AGE = 27;
    private static final int CURRENT_PLAYER_ABILITY = 19;
    private static final int CURRENT_PLAYER_SPRINT_SPEED = 85;
    private static final int UPDATED_PLAYER_ABILITY = 25;
    private static final int UPDATED_PLAYER_SPRINT_SPEED = 87;
    private static final List<String> PLAYER_ATTRIBUTE_CATEGORIES = ImmutableList.of("Technical", "Physical", "Mental");
    private static final List<String> PLAYER_ATTRIBUTE_GROUPS = ImmutableList.of("Attacking", "Aerial", "Vision",
            "Defending", "Speed");
    public static final String CREATED_BY = "fake email";

    private PlayerService playerService;

    @Mock
    private CouchbaseDAO<ResourceKey> couchbaseDAO;

    /**
     * set up test data before each test case is run
     */
    @Before
    public void initialize() {
        MockitoAnnotations.openMocks(this);
        playerService = new PlayerService(couchbaseDAO);
    }

    /**
     * given a valid player id, tests that the player entity is successfully fetched from couchbase server and
     * returned in the response
     */
    @Test
    public void getPlayerFetchesPlayerFromCouchbase() {
        // setup
        UUID playerId = UUID.randomUUID();
        Player playerFromCouchbase = getPlayerDataStub(playerId, true, true, true);
        when(couchbaseDAO.getDocument(any(), any())).thenReturn(playerFromCouchbase);

        // execute
        Player player = playerService.getPlayer(playerId);

        // assert
        verify(couchbaseDAO).getDocument(any(), any());
        assertEquals(playerId, player.getId());
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
        playerService.getPlayer(invalidPlayerId);

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
        Player createdPlayer = playerService.createPlayer(incomingPlayer, CREATED_BY);

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
        assertEquals(CREATED_BY, newPlayer.getCreatedBy());
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
        Player updatedPlayer = playerService.updatePlayer(incomingPlayer, existingPlayerInCouchbase, existingPlayerId);

        // assert
        verify(couchbaseDAO).updateDocument(resourceKeyCaptor.capture(), updatedPlayerCaptor.capture());
        ResourceKey capturedResourceKey = resourceKeyCaptor.getValue();
        assertEquals(existingPlayerId, capturedResourceKey.getResourceId());

        Player updatedPlayerInCouchbase = updatedPlayerCaptor.getValue();
        updatedPlayerInCouchbase.getAttributes().forEach(attribute -> {
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
        assertNotEquals(existingPlayerInCouchbase.getLastModifiedDate(),
                updatedPlayerInCouchbase.getLastModifiedDate());
        assertEquals(CREATED_BY, updatedPlayerInCouchbase.getCreatedBy());

        // TODO: 1/3/2022 consider removing these checks if seems to be redundant 
        assertEquals(incomingPlayer.getMetadata(), updatedPlayer.getMetadata());
        assertEquals(incomingPlayer.getRoles(), updatedPlayer.getRoles());
        assertEquals(incomingPlayer.getAbility(), updatedPlayer.getAbility());
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
        playerService.deletePlayer(playerId);

        // assert
        verify(couchbaseDAO).deleteDocument(resourceKeyCaptor.capture());
        assertEquals(playerId, resourceKeyCaptor.getValue().getResourceId());
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
        playerService.deletePlayer(playerId);

        // assert
        verify(couchbaseDAO).deleteDocument(resourceKeyCaptor.capture());
        assertEquals(playerId, resourceKeyCaptor.getValue().getResourceId());
    }

    // TODO: 1/3/2022 convert this to a test data creator method using builder pattern
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

            playerBuilder.createdBy(CREATED_BY);
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