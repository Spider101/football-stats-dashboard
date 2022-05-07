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
import com.footballstatsdashboard.db.IPlayerEntityDAO;
import com.google.common.collect.ImmutableList;
import io.dropwizard.jackson.Jackson;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.footballstatsdashboard.core.utils.Constants.COUNTRY_CODE_MAPPING_FNAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
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

    private UUID authorizedUserId;
    private PlayerService playerService;

    @Mock
    private IPlayerEntityDAO playerDAO;

    /**
     * set up test data before each test case is run
     */
    @Before
    public void initialize() {
        MockitoAnnotations.openMocks(this);

        authorizedUserId = UUID.randomUUID();
        playerService = new PlayerService(playerDAO);
    }

    /**
     * given a valid player id, tests that the player entity is successfully fetched from the DAO layer
     */
    @Test
    public void getPlayerFetchesPlayerData() {
        // setup
        UUID playerId = UUID.randomUUID();
        Player existingPlayerData = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(true)
                .withId(playerId)
                .withMetadata()
                .withAbility()
                .withRoles()
                .withAttributes()
                .build();
        when(playerDAO.getEntity(eq(playerId))).thenReturn(existingPlayerData);

        // execute
        Player player = playerService.getPlayer(playerId);

        // assert
        verify(playerDAO).getEntity(any());
        assertEquals(playerId, player.getId());
    }

    /**
     * given an invalid player id, tests that the EntityNotFound exception thrown by the DAO layer is handled and a
     * ServiceException is thrown instead
     */
    @Test
    public void getPlayerWhenPlayerDataCannotBeFound() {
        // setup
        UUID invalidPlayerId = UUID.randomUUID();
        when(playerDAO.getEntity(eq(invalidPlayerId))).thenThrow(EntityNotFoundException.class);

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> playerService.getPlayer(invalidPlayerId));

        // assert
        verify(playerDAO).getEntity(any());
        assertEquals(HttpStatus.NOT_FOUND_404, serviceException.getResponseStatus());
    }

    /**
     * given a valid player entity, tests that the internal fields are set correctly on the entity and persisted
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
     * given a player entity without player roles, tests that no data is persisted and a service exception is thrown
     * instead
     */
    @Test
    public void createPlayerWhenPlayerRolesAreNotProvided() {
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
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> playerService.createPlayer(incomingPlayer, existingClub, CREATED_BY));

        // assert
        verify(playerDAO, never()).insertEntity(any());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422, serviceException.getResponseStatus());
    }

    /**
     * given a player entity without player attributes, tests that no data is persisted and a service exception is
     * thrown instead
     */
    @Test
    public void createPlayerWhenPlayerAttributesAreNotProvided() {
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
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> playerService.createPlayer(incomingPlayer, existingClub, CREATED_BY));

        // assert
        verify(playerDAO, never()).insertEntity(any());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422, serviceException.getResponseStatus());
    }

    /**
     * given a player entity without attributes of a particular category like TECHNICAL, tests that no data is
     * persisted and a service exception is thrown instead
     */
    @Test
    public void createPlayerWhenTechnicalPlayerAttributesAreNotProvided() {
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
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> playerService.createPlayer(incomingPlayer, existingClub, CREATED_BY));

        // assert
        verify(playerDAO, never()).insertEntity(any());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422, serviceException.getResponseStatus());
    }

    /**
     * given a valid player entity and an identifier for an existing player entity, tests that the corresponding player
     * entity is updated with the incoming properties and persisted in the DAO layer
     */
    @Test
    public void updatePlayerUpdatesPlayerData() {
        // setup
        UUID existingPlayerId = UUID.randomUUID();
        Player existingPlayerData = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(true)
                .withId(existingPlayerId)
                .withMetadata()
                .withAbility()
                .withRoles()
                .withAttributes()
                .build();
        when(playerDAO.getEntity(eq(existingPlayerId))).thenReturn(existingPlayerData);
        when(playerDAO.doesEntityBelongToUser(eq(existingPlayerId), eq(authorizedUserId))).thenReturn(true);

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
        Player updatedPlayer = playerService.updatePlayer(incomingPlayer, existingPlayerId, authorizedUserId);

        // assert
        verify(playerDAO).getEntity(any());
        verify(playerDAO).doesEntityBelongToUser(any(), any());
        verify(playerDAO).updateEntity(any(), any());

        updatedPlayer.getAttributes().forEach(attribute -> {
            Attribute existingPlayerAttribute = existingPlayerData.getAttributes().stream()
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
        assertEquals(existingPlayerData.getAbility().getHistory().size() + 1,
                updatedPlayer.getAbility().getHistory().size());
        assertEquals(incomingPlayer.getRoles(), updatedPlayer.getRoles());

        // assertions for general house-keeping fields
        assertNotEquals(existingPlayerData.getLastModifiedDate(), updatedPlayer.getLastModifiedDate());
        assertEquals(CREATED_BY, updatedPlayer.getCreatedBy());
    }

    /**
     * given incoming player data for a player entity that does not exist, tests that no player data is updated and a
     * service exception is thrown
     */
    @Test
    public void updatePlayerWhenPlayerDoesNotExist() {
        // setup
        UUID existingPlayerId = UUID.randomUUID();
        when(playerDAO.getEntity(eq(existingPlayerId))).thenThrow(EntityNotFoundException.class);

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
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> playerService.updatePlayer(incomingPlayer, existingPlayerId, authorizedUserId));

        // assert
        verify(playerDAO).getEntity(any());
        verify(playerDAO, never()).doesEntityBelongToUser(any(), any());
        verify(playerDAO, never()).updateEntity(any(), any());
        assertEquals(HttpStatus.NOT_FOUND_404, serviceException.getResponseStatus());
    }

    /**
     * given incoming player data for a player entity the use does not have access to, tests that the corresponding
     * player data is updated and a service exception is thrown instead
     */
    @Test
    public void updatePlayerWhenPlayerDoesNotBelongToUser() {
        // setup
        UUID existingPlayerId = UUID.randomUUID();
        Player existingPlayerData = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(true)
                .withId(existingPlayerId)
                .withMetadata()
                .withAbility()
                .withRoles()
                .withAttributes()
                .build();
        when(playerDAO.getEntity(eq(existingPlayerId))).thenReturn(existingPlayerData);
        when(playerDAO.doesEntityBelongToUser(eq(existingPlayerId), eq(authorizedUserId))).thenReturn(false);

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
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> playerService.updatePlayer(incomingPlayer, existingPlayerId, authorizedUserId));

        // assert
        verify(playerDAO).getEntity(any());
        verify(playerDAO).doesEntityBelongToUser(any(), any());
        verify(playerDAO, never()).updateEntity(any(), any());
        assertEquals(HttpStatus.FORBIDDEN_403, serviceException.getResponseStatus());
    }

    /**
     * given a player entity without roles, tests that the corresponding player data is not updated and a service
     * exception is thrown instead
     */
    @Test
    public void updatePlayerWhenPlayerRolesAreNotProvided() {
        // setup
        UUID existingPlayerId = UUID.randomUUID();
        Player existingPlayerData = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(true)
                .withId(existingPlayerId)
                .withMetadata()
                .withAbility()
                .withRoles()
                .withAttributes()
                .build();
        when(playerDAO.getEntity(eq(existingPlayerId))).thenReturn(existingPlayerData);
        when(playerDAO.doesEntityBelongToUser(eq(existingPlayerId), eq(authorizedUserId))).thenReturn(true);

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
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> playerService.updatePlayer(incomingPlayer, existingPlayerId, authorizedUserId));

        // assert
        verify(playerDAO).getEntity(any());
        verify(playerDAO).doesEntityBelongToUser(any(), any());
        verify(playerDAO, never()).updateEntity(any(), any());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422, serviceException.getResponseStatus());
    }

    /**
     * given a player entity without attributes data, tests that the corresponding player data is not updated and a
     * service exception is thrown instead
     */
    @Test
    public void updatePlayerWhenPlayerAttributesAreNotProvided() {
        // setup
        UUID existingPlayerId = UUID.randomUUID();
        Player existingPlayerData = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(true)
                .withId(existingPlayerId)
                .withMetadata()
                .withAbility()
                .withRoles()
                .withAttributes()
                .build();
        when(playerDAO.getEntity(eq(existingPlayerId))).thenReturn(existingPlayerData);
        when(playerDAO.doesEntityBelongToUser(eq(existingPlayerId), eq(authorizedUserId))).thenReturn(true);

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
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> playerService.updatePlayer(incomingPlayer, existingPlayerId, authorizedUserId));

        // assert
        verify(playerDAO).getEntity(any());
        verify(playerDAO).doesEntityBelongToUser(any(), any());
        verify(playerDAO, never()).updateEntity(any(), any());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422, serviceException.getResponseStatus());
    }

    /**
     * given a player entity with an invalid attribute name, tests that the corresponding player data is not updated
     * and a service exception is thrown instead
     */
    @Test
    public void updatePlayerWhenAttributeNameIsInvalid() {
        // setup
        UUID existingPlayerId = UUID.randomUUID();
        Player existingPlayerData = PlayerDataProvider.PlayerBuilder.builder()
                .isExistingPlayer(true)
                .withId(existingPlayerId)
                .withMetadata()
                .withAbility()
                .withRoles()
                .withAttributes()
                .build();
        when(playerDAO.getEntity(eq(existingPlayerId))).thenReturn(existingPlayerData);
        when(playerDAO.doesEntityBelongToUser(eq(existingPlayerId), eq(authorizedUserId))).thenReturn(true);

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
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> playerService.updatePlayer(incomingPlayer, existingPlayerId, authorizedUserId));

        // assert
        verify(playerDAO).getEntity(any());
        verify(playerDAO).doesEntityBelongToUser(any(), any());
        verify(playerDAO, never()).updateEntity(any(), any());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422, serviceException.getResponseStatus());
    }

    /**
     * given a valid player id, removes the player entity from the DAO layer
     */
    @Test
    public void deletePlayerRemovesPlayerData() {
        // setup
        UUID playerId = UUID.randomUUID();
        when(playerDAO.doesEntityExist(eq(playerId))).thenReturn(true);
        when(playerDAO.doesEntityBelongToUser(eq(playerId), eq(authorizedUserId))).thenReturn(true);

        // execute
        playerService.deletePlayer(playerId, authorizedUserId);

        // assert
        verify(playerDAO).doesEntityExist(any());
        verify(playerDAO).doesEntityBelongToUser(any(), any());
        verify(playerDAO).deleteEntity(eq(playerId));
    }

    /**
     * given an invalid player id, tests that the EntityNotFound exception thrown by the DAO layer is handled and a
     * ServiceException is thrown instead
     */
    @Test
    public void deletePlayerWhenPlayerNotFound() {
        // setup
        UUID nonExistentPlayerId = UUID.randomUUID();
        when(playerDAO.doesEntityExist(nonExistentPlayerId)).thenReturn(false);

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> playerService.deletePlayer(nonExistentPlayerId, authorizedUserId));

        // assert
        verify(playerDAO).doesEntityExist(any());
        verify(playerDAO, never()).doesEntityBelongToUser(any(), any());
        verify(playerDAO, never()).deleteEntity(any());
        assertEquals(HttpStatus.NOT_FOUND_404, serviceException.getResponseStatus());
    }

    /**
     * given a player id for a player entity with an invalid club id, tests that the NoResultException thrown by the
     * DAO layer is handled and a ServiceException is thrown instead
     */
    @Test
    public void deletePlayerWhenAssociatedClubNotFound() {
        // setup
        UUID inaccessiblePlayerId = UUID.randomUUID();
        when(playerDAO.doesEntityExist(eq(inaccessiblePlayerId))).thenReturn(true);
        when(playerDAO.doesEntityBelongToUser(eq(inaccessiblePlayerId), eq(authorizedUserId)))
                .thenThrow(NoResultException.class);

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> playerService.deletePlayer(inaccessiblePlayerId, authorizedUserId));

        // assert
        verify(playerDAO).doesEntityExist(any());
        verify(playerDAO).doesEntityBelongToUser(any(), any());
        verify(playerDAO, never()).deleteEntity(any());
        assertEquals(HttpStatus.FORBIDDEN_403, serviceException.getResponseStatus());
    }

    /**
     * given an id for a player that the user does not have access to, tests that the player data is not deleted and a
     * service exception is thrown instead
     */
    @Test
    public void deletePlayerWhenUserDoesNotHaveAccessToPlayer() {
        // setup
        UUID inaccessiblePlayerId = UUID.randomUUID();
        when(playerDAO.doesEntityExist(eq(inaccessiblePlayerId))).thenReturn(true);
        when(playerDAO.doesEntityBelongToUser(eq(inaccessiblePlayerId), eq(authorizedUserId))).thenReturn(false);

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> playerService.deletePlayer(inaccessiblePlayerId, authorizedUserId));

        // assert
        verify(playerDAO).doesEntityExist(any());
        verify(playerDAO).doesEntityBelongToUser(any(), any());
        verify(playerDAO, never()).deleteEntity(any());
        assertEquals(HttpStatus.FORBIDDEN_403, serviceException.getResponseStatus());
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