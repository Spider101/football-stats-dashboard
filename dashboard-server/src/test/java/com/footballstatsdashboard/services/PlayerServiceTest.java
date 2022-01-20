package com.footballstatsdashboard.services;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.footballstatsdashboard.ClubDataProvider;
import com.footballstatsdashboard.PlayerDataProvider;
import com.footballstatsdashboard.api.model.CountryCodeMetadata;
import com.footballstatsdashboard.api.model.Player;
import com.footballstatsdashboard.api.model.Club;
import com.footballstatsdashboard.api.model.player.Attribute;
import com.footballstatsdashboard.api.model.player.Metadata;
import com.footballstatsdashboard.core.exceptions.ServiceException;
import com.footballstatsdashboard.core.utils.FixtureLoader;
import com.footballstatsdashboard.db.CouchbaseDAO;
import com.footballstatsdashboard.db.key.ResourceKey;
import com.google.common.collect.ImmutableList;
import io.dropwizard.jackson.Jackson;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PlayerServiceTest {
    private static final int UPDATED_PLAYER_SPRINT_SPEED = 87;
    private static final List<String> PLAYER_ATTRIBUTE_CATEGORIES = ImmutableList.of("Technical", "Physical", "Mental");
    private static final List<String> PLAYER_ATTRIBUTE_GROUPS = ImmutableList.of("Attacking", "Aerial", "Vision",
            "Defending", "Speed");
    private static final String CREATED_BY = "fake email";
    private static final FixtureLoader FIXTURE_LOADER = new FixtureLoader(Jackson.newObjectMapper().copy());

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
     * given an invalid player id, tests that the DocumentNotFound exception thrown by the DAO layer is handled and a
     * ServiceException is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void getPlayerWhenPlayerNotFoundInCouchbase() {
        // setup
        UUID invalidPlayerId = UUID.randomUUID();
        when(couchbaseDAO.getDocument(any(), any())).thenThrow(DocumentNotFoundException.class);

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
    public void createPlayerPersistsPlayerInCouchbase() throws IOException {
        // setup
        Player incomingPlayer = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(false)
                .withMetadata()
                .withRoles()
                .withAttributes()
                .build();
        ArgumentCaptor<Player> newPlayerCaptor = ArgumentCaptor.forClass(Player.class);

        Club existingClub = ClubDataProvider.ClubBuilder.builder()
                .isExisting(true)
                .existingUserId(UUID.randomUUID())
                .withId(UUID.randomUUID())
                .build();

        // execute
        Player createdPlayer = playerService.createPlayer(incomingPlayer, existingClub, CREATED_BY);

        // assert
        verify(couchbaseDAO).insertDocument(any(), newPlayerCaptor.capture());
        Player newPlayer = newPlayerCaptor.getValue();
        assertEquals(createdPlayer, newPlayer);

        assertEquals(existingClub.getName(), createdPlayer.getMetadata().getClub());
        assertNotNull(createdPlayer.getMetadata().getClubLogo());

        assertCountryLogo(createdPlayer.getMetadata());

        assertNotNull(createdPlayer.getAbility().getCurrent());
        assertEquals(1, createdPlayer.getAbility().getHistory().size());

        createdPlayer.getAttributes().forEach(attribute -> {
            assertTrue(attribute.getCategory() != null
                    && PLAYER_ATTRIBUTE_CATEGORIES.contains(attribute.getCategory()));
            assertTrue(attribute.getGroup() != null
                    && PLAYER_ATTRIBUTE_GROUPS.contains(attribute.getGroup()));
            assertNotNull(attribute.getGroup());
            assertEquals(1, attribute.getHistory().size());
            assertEquals(attribute.getValue(), attribute.getHistory().get(0));
        });

        // assertions for general house-keeping fields
        assertNotNull(createdPlayer.getCreatedDate());
        assertNotNull(createdPlayer.getLastModifiedDate());
        assertEquals(CREATED_BY, createdPlayer.getCreatedBy());
    }

    /**
     * given that the request contains a player entity without player roles, tests that no data is persisted in
     * couchbase and a service exception is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void createPlayerWhenPlayerRolesAreNotProvided() throws IOException {
        // setup
        Player incomingPlayer = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(false)
                .withMetadata()
                .withAbility()
                .withAttributes()
                .build();
        Club existingClub = ClubDataProvider.ClubBuilder.builder()
                .isExisting(true)
                .existingUserId(UUID.randomUUID())
                .withId(UUID.randomUUID())
                .build();

        // execute
        playerService.createPlayer(incomingPlayer, existingClub, CREATED_BY);

        // assert
        verify(couchbaseDAO, never()).insertDocument(any(), any());
    }

    /**
     * given that the request contains a player entity without player attributes, tests that no data is persisted in
     * couchbase and a service exception is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void createPlayerWhenPlayerAttributesAreNotProvided() throws IOException {
        // setup
        Player incomingPlayer = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(false)
                .withMetadata()
                .withAbility()
                .withRoles()
                .build();
        Club existingClub = ClubDataProvider.ClubBuilder.builder()
                .isExisting(true)
                .existingUserId(UUID.randomUUID())
                .withId(UUID.randomUUID())
                .build();

        // execute
        playerService.createPlayer(incomingPlayer, existingClub, CREATED_BY);

        // assert
        verify(couchbaseDAO, never()).insertDocument(any(), any());
    }

    /**
     * given that the request contains a player entity without attributes of a particular category like TECHNICAL,
     * tests that no data is persisted in couchbase and a service exception is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void createPlayerWhenTechnicalPlayerAttributesAreNotProvided() throws IOException {
        // setup
        Player incomingPlayer = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(false)
                .withMetadata()
                .withAbility()
                .withPhysicalAttributes()
                .withMentalAttributes()
                .withRoles()
                .build();
        Club existingClub = ClubDataProvider.ClubBuilder.builder()
                .isExisting(true)
                .existingUserId(UUID.randomUUID())
                .withId(UUID.randomUUID())
                .build();

        // execute
        playerService.createPlayer(incomingPlayer, existingClub, CREATED_BY);

        // assert
        verify(couchbaseDAO, never()).insertDocument(any(), any());
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
        when(couchbaseDAO.getDocument(any(), any())).thenReturn(existingPlayerInCouchbase);

        Player incomingPlayerBase = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(false)
                .withId(existingPlayerId)
                .withMetadata()
                .withRoles()
                .withAttributes()
                .build();
        Player incomingPlayer = PlayerDataProvider.ModifiedPlayerBuilder.builder()
                .from(incomingPlayerBase)
                .withUpdatedNameInMetadata("updated name")
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
        assertNotNull(updatedPlayer.getAbility());
        assertEquals(existingPlayerInCouchbase.getAbility().getHistory().size() + 1,
                updatedPlayer.getAbility().getHistory().size());
        assertEquals(incomingPlayer.getRoles(), updatedPlayer.getRoles());

        // assertions for general house-keeping fields
        assertNotEquals(existingPlayerInCouchbase.getLastModifiedDate(), updatedPlayer.getLastModifiedDate());
        assertEquals(CREATED_BY, updatedPlayer.getCreatedBy());
    }

    /**
     * given a player entity with attributes having invalid category and group information in the request, tests that
     * the category and group names are derived from the existing data in couchbase instead of what is present in the
     * request before updating the player data in couchbase
     */
    @Test
    public void updatePlayerWithAttributesHavingInvalidCategoryAndGroupNames() {
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
        when(couchbaseDAO.getDocument(any(), any())).thenReturn(existingPlayerInCouchbase);

        Player incomingPlayerBase = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(false)
                .hasAttributeWithInvalidCategory(true)
                .hasAttributeWithInvalidGroup(true)
                .withId(existingPlayerId)
                .withMetadata()
                .withRoles()
                .withTechnicalAttributes()
                .withPhysicalAttributes()
                .withMentalAttributes()
                .build();
        Player incomingPlayer = PlayerDataProvider.ModifiedPlayerBuilder.builder()
                .from(incomingPlayerBase)
                .withUpdatedNameInMetadata("updated name")
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
                    .findFirst()
                    .orElse(null);
            assertNotNull(existingPlayerAttribute);
            assertEquals(existingPlayerAttribute.getCategory(), attribute.getCategory());
            assertEquals(existingPlayerAttribute.getGroup(), attribute.getGroup());
        });
    }

    /**
     * given a player entity without roles data in the request, tests that the corresponding player data in couchbase is
     * not updated and a service exception is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void updatePlayerWhenPlayerRolesAreNotProvided() {
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
        when(couchbaseDAO.getDocument(any(), any())).thenReturn(existingPlayerInCouchbase);

        Player incomingPlayerBase = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(false)
                .withId(existingPlayerId)
                .withMetadata()
                .withAttributes()
                .build();
        Player incomingPlayer = PlayerDataProvider.ModifiedPlayerBuilder.builder()
                .from(incomingPlayerBase)
                .withUpdatedNameInMetadata("updated name")
                .withUpdatedAttributeValue("sprint speed", UPDATED_PLAYER_SPRINT_SPEED)
                .build();

        // execute
        playerService.updatePlayer(incomingPlayer, existingPlayerInCouchbase, existingPlayerId);

        // assert
        verify(couchbaseDAO, never()).updateDocument(any(), any());
    }

    /**
     * given a player entity without attributes data in the request, tests that the corresponding player data in
     * couchbase is not updated and a service exception is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void updatePlayerWhenPlayerAttributesAreNotProvided() {
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
        when(couchbaseDAO.getDocument(any(), any())).thenReturn(existingPlayerInCouchbase);

        Player incomingPlayerBase = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(false)
                .withId(existingPlayerId)
                .withMetadata()
                .withRoles()
                .build();
        Player incomingPlayer = PlayerDataProvider.ModifiedPlayerBuilder.builder()
                .from(incomingPlayerBase)
                .withUpdatedNameInMetadata("updated name")
                .withUpdatedRoleName("updated role name")
                .build();

        // execute
        playerService.updatePlayer(incomingPlayer, existingPlayerInCouchbase, existingPlayerId);

        // assert
        verify(couchbaseDAO, never()).updateDocument(any(), any());
    }

    /**
     * given a player entity with an invalid attribute in the request, tests that the corresponding player data in
     * couchbase is not updated and a service exception is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void updatePlayerWhenPlayerAttributeIncludesInvalidAttribute() {
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
        when(couchbaseDAO.getDocument(any(), any())).thenReturn(existingPlayerInCouchbase);

        Player incomingPlayerBase = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(false)
                .withId(existingPlayerId)
                .withMetadata()
                .withAttributes()
                .withInvalidAttributes()
                .withRoles()
                .build();
        Player incomingPlayer = PlayerDataProvider.ModifiedPlayerBuilder.builder()
                .from(incomingPlayerBase)
                .withUpdatedNameInMetadata("updated name")
                .withUpdatedRoleName("updated role name")
                .build();

        // execute
        playerService.updatePlayer(incomingPlayer, existingPlayerInCouchbase, existingPlayerId);

        // assert
        verify(couchbaseDAO, never()).updateDocument(any(), any());
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

    private void assertCountryLogo(Metadata createdPlayerMetadata) throws IOException {
        TypeReference<List<CountryCodeMetadata>> countryCodeMetadataTypeRef = new TypeReference<>() { };
        List<CountryCodeMetadata> countryCodeMetadataList =
                FIXTURE_LOADER.loadFixture("countryCodeMapping.json", countryCodeMetadataTypeRef);
        String countryCode = countryCodeMetadataList.stream()
                .filter(countryCodeMetadata ->
                        countryCodeMetadata.getCountryName().equals(createdPlayerMetadata.getCountry()))
                .findFirst()
                .map(CountryCodeMetadata::getCountryCode)
                .orElse("");
        assertNotNull(createdPlayerMetadata.getCountryLogo());
        assertTrue(createdPlayerMetadata.getCountryLogo().contains(countryCode));
    }
}