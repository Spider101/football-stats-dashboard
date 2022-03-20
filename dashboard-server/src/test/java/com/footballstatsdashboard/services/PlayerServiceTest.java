package com.footballstatsdashboard.services;

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
import com.footballstatsdashboard.db.IEntityDAO;
import com.google.common.collect.ImmutableList;
import io.dropwizard.jackson.Jackson;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.footballstatsdashboard.core.utils.Constants.COUNTRY_CODE_MAPPING_FNAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PlayerServiceTest {
    private static final int UPDATED_PLAYER_SPRINT_SPEED = 87;
    private static final String CREATED_BY = "fake email";
    private static final FixtureLoader FIXTURE_LOADER = new FixtureLoader(Jackson.newObjectMapper().copy());

    private PlayerService playerService;

    @Mock
    private IEntityDAO<Player> playerDAO;

    /**
     * set up test data before each test case is run
     */
    @Before
    public void initialize() {
        MockitoAnnotations.openMocks(this);
        playerService = new PlayerService(playerDAO);
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
        when(playerDAO.getEntity(eq(playerId))).thenReturn(playerFromCouchbase);

        // execute
        Player player = playerService.getPlayer(playerId);

        // assert
        verify(playerDAO).getEntity(any());
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
        when(playerDAO.getEntity(eq(invalidPlayerId))).thenThrow(EntityNotFoundException.class);

        // execute
        playerService.getPlayer(invalidPlayerId);

        // assert
        verify(playerDAO).getEntity(any());
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
        verify(playerDAO).insertEntity(newPlayerCaptor.capture());
        Player newPlayer = newPlayerCaptor.getValue();
        assertEquals(createdPlayer, newPlayer);

        assertEquals(existingClub.getName(), createdPlayer.getMetadata().getClub());
        assertNotNull(createdPlayer.getMetadata().getClubLogo());

        assertCountryLogo(createdPlayer.getMetadata());

        assertNotNull(createdPlayer.getAbility());
        assertNotNull(createdPlayer.getAbility().getCurrent());
        assertEquals(1, createdPlayer.getAbility().getHistory().size());

        createdPlayer.getAttributes().forEach(attribute -> {
            assertNotNull(attribute.getCategory());
            assertNotNull(attribute.getGroup());
            assertNotNull(attribute.getHistory());
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
        verify(playerDAO, never()).insertEntity(any());
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
        verify(playerDAO, never()).insertEntity(any());
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
        verify(playerDAO, never()).insertEntity(any());
    }

    /**
     * given a valid player entity in the request, tests that an updated player entity with updated internal fields
     * is upserted in couchbase
     */
    @Test
    public void updatePlayerUpdatesPlayerInCouchbase() {
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
        when(playerDAO.getEntity(eq(existingPlayerId))).thenReturn(existingPlayerInCouchbase);

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
        verify(playerDAO).updateEntity(eq(existingPlayerId), any());
        updatedPlayer.getAttributes().forEach(attribute -> {
            Attribute existingPlayerAttribute = existingPlayerInCouchbase.getAttributes().stream()
                    .filter(existingAttribute -> existingAttribute.getName().equals(attribute.getName()))
                    .findFirst().orElse(null);
            assertNotNull(existingPlayerAttribute);
            assertNotNull(attribute.getCategory());
            assertNotNull(attribute.getGroup());
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
        when(playerDAO.getEntity(eq(existingPlayerId))).thenReturn(existingPlayerInCouchbase);

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
        verify(playerDAO, never()).updateEntity(any(), any());
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
        when(playerDAO.getEntity(eq(existingPlayerId))).thenReturn(existingPlayerInCouchbase);

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
        verify(playerDAO, never()).updateEntity(any(), any());
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
        when(playerDAO.getEntity(eq(existingPlayerId))).thenReturn(existingPlayerInCouchbase);

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
        verify(playerDAO, never()).updateEntity(any(), any());
    }

    /**
     * given a valid player id, removes the player entity from couchbase
     */
    @Test
    public void deletePlayerRemovesPlayerFromCouchbase() {
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
        when(playerDAO.getEntity(eq(playerId))).thenReturn(playerFromCouchbase);

        // execute
        playerService.deletePlayer(playerId, clubId -> true);

        // assert
        verify(playerDAO).getEntity(any());
        verify(playerDAO).deleteEntity(eq(playerId));
    }

    /**
     * given an invalid player id, tests that the DocumentNotFound exception thrown by the DAO layer is handled and a
     * ServiceException is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void deletePlayerWhenPlayerNotFound() {
        // setup
        UUID invalidPlayerId = UUID.randomUUID();
        when(playerDAO.getEntity(eq(invalidPlayerId))).thenThrow(EntityNotFoundException.class);

        // execute
        playerService.deletePlayer(invalidPlayerId, clubId -> true);

        // assert
        verify(playerDAO).getEntity(any());
        verify(playerDAO, never()).deleteEntity(any());
    }

    /**
     * given an id for a player that the user does not have access to, tests that the player data is not deleted and a
     * service exception is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void deletePlayerWhenUserDoesNotHaveAccessToPlayer() {
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
        when(playerDAO.getEntity(eq(playerId))).thenReturn(playerFromCouchbase);

        // execute
        playerService.deletePlayer(playerId, clubId -> false);

        // assert
        verify(playerDAO, never()).deleteEntity(any());
    }

    private void assertCountryLogo(Metadata createdPlayerMetadata) throws IOException {
        TypeReference<List<CountryCodeMetadata>> countryCodeMetadataTypeRef = new TypeReference<>() { };
        List<CountryCodeMetadata> countryCodeMetadataList =
                FIXTURE_LOADER.loadFixture(COUNTRY_CODE_MAPPING_FNAME, countryCodeMetadataTypeRef);
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