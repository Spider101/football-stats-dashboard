package com.footballstatsdashboard.services;

import com.footballstatsdashboard.PlayerDataProvider;
import com.footballstatsdashboard.api.model.Player;
import com.footballstatsdashboard.api.model.club.Club;
import com.footballstatsdashboard.api.model.club.ImmutableClub;
import com.footballstatsdashboard.api.model.player.Attribute;
import com.footballstatsdashboard.db.CouchbaseDAO;
import com.footballstatsdashboard.db.key.ResourceKey;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
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
        Player playerFromCouchbase = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(true)
                .withId(playerId)
                .withMetadata()
                .withAbility()
                .withRoles()
                .withAttributes()
                .build();
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
        Player incomingPlayer = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(false)
                .withMetadata()
                .withAbility()
                .withRoles()
                .withAttributes()
                .build();
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
        ArgumentCaptor<ResourceKey> resourceKeyCaptor = ArgumentCaptor.forClass(ResourceKey.class);

        UUID existingPlayerId = UUID.randomUUID();
        Player existingPlayerInCouchbase = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(true)
                .withId(existingPlayerId)
                .withMetadata()
                .withAbility()
                .withRoles()
                .withAttributes()
                .build();
        Player incomingPlayerBase = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(false)
                .withId(existingPlayerId)
                .withMetadata()
                .withAbility()
                .withRoles()
                .withAttributes()
                .build();
        when(couchbaseDAO.getDocument(any(), any())).thenReturn(existingPlayerInCouchbase);

        Player incomingPlayer = PlayerDataProvider.ModifiedPlayerBuilder.builder()
                .from(incomingPlayerBase)
                .withUpdatedNameInMetadata("updated name")
                .withUpdatedCurrentAbility(UPDATED_PLAYER_ABILITY)
                .withUpdatedRoleName("updated role name")
                .withUpdatedAttributeValue("sprint speed", UPDATED_PLAYER_SPRINT_SPEED)
                .build();

        // execute
        Player updatedPlayer = playerService.updatePlayer(incomingPlayer, existingPlayerInCouchbase, existingPlayerId);

        // assert
        verify(couchbaseDAO).updateDocument(resourceKeyCaptor.capture(), any());
        ResourceKey capturedResourceKey = resourceKeyCaptor.getValue();
        assertEquals(existingPlayerId, capturedResourceKey.getResourceId());

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

        assertEquals(incomingPlayer.getMetadata().getName(), updatedPlayer.getMetadata().getName());
        assertEquals(incomingPlayer.getAbility().getCurrent(), updatedPlayer.getAbility().getCurrent());
        assertEquals(existingPlayerInCouchbase.getAbility().getHistory().size() + 1,
                updatedPlayer.getAbility().getHistory().size());
        assertEquals(incomingPlayer.getRoles(), updatedPlayer.getRoles());

        assertNotEquals(existingPlayerInCouchbase.getLastModifiedDate(), updatedPlayer.getLastModifiedDate());
        assertEquals(CREATED_BY, updatedPlayer.getCreatedBy());
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
}