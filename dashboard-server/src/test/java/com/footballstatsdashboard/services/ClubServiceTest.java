package com.footballstatsdashboard.services;

import com.footballstatsdashboard.ClubDataProvider;
import com.footballstatsdashboard.api.model.Club;
import com.footballstatsdashboard.api.model.club.ClubSummary;
import com.footballstatsdashboard.api.model.club.ImmutableSquadPlayer;
import com.footballstatsdashboard.api.model.club.SquadPlayer;
import com.footballstatsdashboard.core.exceptions.ServiceException;
import com.footballstatsdashboard.db.IClubEntityDAO;
import com.google.common.collect.ImmutableList;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClubServiceTest {
    private static final String USER_EMAIL = "fake email";
    private static final int CURRENT_PLAYER_ABILITY = 19;

    private UUID userId;
    private ClubService clubService;

    @Mock
    private IClubEntityDAO clubDAO;

    /**
     * set up test data before each test case is run
     */
    @Before
    public void initialize() {
        MockitoAnnotations.openMocks(this);

        userId = UUID.randomUUID();
        clubService = new ClubService(clubDAO);
    }

    /**
     * given a valid club id, tests that the club entity is successfully fetched from the DAO layer
     */
    @Test
    public void getClubFetchesClubData() {
        // setup
        UUID clubId = UUID.randomUUID();
        Club existingClubData = ClubDataProvider.ClubBuilder.builder()
                .isExisting(true)
                .existingUserId(userId)
                .withId(clubId)
                .withIncome()
                .withExpenditure()
                .build();
        when(clubDAO.getEntity(eq(clubId))).thenReturn(existingClubData);

        // execute
        Club club = clubService.getClub(clubId, userId);

        // assert
        verify(clubDAO).getEntity(any());

        assertEquals(clubId, club.getId());
        assertNotNull(club.getIncome());
        assertNotNull(club.getExpenditure());
        assertNotNull(club.getUserId());
        assertEquals(userId, club.getUserId());
    }

    /**
     * given an invalid club id, tests that the EntityNotFound exception thrown by the DAO layer is handled and a
     * ServiceException is thrown instead
     */
    @Test
    public void getClubWhenClubDataCannotBeFound() {
        // setup
        UUID invalidClubId = UUID.randomUUID();
        when(clubDAO.getEntity(eq(invalidClubId))).thenThrow(EntityNotFoundException.class);

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> clubService.getClub(invalidClubId, userId));

        // assert
        verify(clubDAO).getEntity(any());
        assertEquals(HttpStatus.NOT_FOUND_404, serviceException.getResponseStatus());
    }

    /**
     * given a club id for a club the user does not have access to, tests that the club data is not returned and a
     * service exception is thrown instead
     */
    @Test
    public void getClubWhenClubDoesNotBelongToUser() {
        // setup
        UUID clubId = UUID.randomUUID();
        Club existingClubData = ClubDataProvider.ClubBuilder.builder()
                .isExisting(true)
                .existingUserId(UUID.randomUUID())
                .withId(clubId)
                .withIncome()
                .withExpenditure()
                .build();
        when(clubDAO.getEntity(eq(clubId))).thenReturn(existingClubData);

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> clubService.getClub(clubId, userId));

        // assert
        verify(clubDAO).getEntity(any());
        assertEquals(HttpStatus.FORBIDDEN_403, serviceException.getResponseStatus());
    }

    /**
     * given a valid club entity, tests that the internal fields are set correctly on the entity and persisted
     */
    @Test
    public void createClubPersistsClubData() {
        // setup
        Club incomingClub = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .withIncome()
                .withExpenditure()
                .build();
        ArgumentCaptor<Club> newClubCaptor = ArgumentCaptor.forClass(Club.class);

        // execute
        Club createdClub = clubService.createClub(incomingClub, userId, USER_EMAIL);

        // assert
        verify(clubDAO).insertEntity(newClubCaptor.capture());
        Club newClub = newClubCaptor.getValue();
        assertEquals(createdClub, newClub);

        assertEquals(incomingClub.getId(), createdClub.getId());

        // verify manager funds, income and expenditure histories are initialized during creation
        assertNotNull(createdClub.getManagerFunds().getHistory());
        assertEquals(1, createdClub.getManagerFunds().getHistory().size());

        assertNotNull(createdClub.getIncome());
        assertNotNull(createdClub.getIncome().getHistory());
        assertEquals(1, createdClub.getIncome().getHistory().size());

        assertNotNull(createdClub.getExpenditure());
        assertNotNull(createdClub.getExpenditure().getHistory());
        assertEquals(1, createdClub.getExpenditure().getHistory().size());

        // assertions for general house-keeping fields
        assertNotNull(createdClub.getCreatedDate());
        assertNotNull(createdClub.getLastModifiedDate());
        assertEquals(USER_EMAIL, createdClub.getCreatedBy());
        assertEquals(userId, createdClub.getUserId());
    }

    /**
     * given a club entity with an empty club name, tests that no data is persisted and a service exception is thrown
     * instead
     */
    @Test
    public void createClubWhenClubNameIsEmpty() {
        // setup
        Club incomingClubWithNoName = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .customClubName("")
                .withIncome()
                .withExpenditure()
                .build();

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> clubService.createClub(incomingClubWithNoName, userId, USER_EMAIL));

        // assert
        verify(clubDAO, never()).insertEntity(any());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422, serviceException.getResponseStatus());
    }

    /**
     * given a club entity without valid income data, tests that no data is persisted and a service exception is thrown
     * instead
     */
    @Test
    public void createClubWithoutIncomeData() {
        // setup
        Club incomingClubWithNoIncomeData = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .withExpenditure()
                .build();

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> clubService.createClub(incomingClubWithNoIncomeData, userId, USER_EMAIL));

        // assert
        verify(clubDAO, never()).insertEntity(any());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422, serviceException.getResponseStatus());
    }

    /**
     * given a club entity without valid expenditure data, tests that no data is persisted and a service exception is
     * thrown instead
     */
    @Test
    public void createClubWithoutExpenditureData() {
        // setup
        Club incomingClubWithNoExpenditureData = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .withIncome()
                .build();

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> clubService.createClub(incomingClubWithNoExpenditureData, userId, USER_EMAIL));

        // assert
        verify(clubDAO, never()).insertEntity(any());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422, serviceException.getResponseStatus());
    }

    /**
     * given a club entity whose manager funds is greater than the transfer and wage budgets set, tests that no data is
     * persisted and a service exception is thrown instead
     */
    @Test
    public void createClubWithIncorrectManagerFunds() {
        // setup
        Club incomingClubWithIncorrectManagerFunds = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .customManagerFunds(new BigDecimal("10000"))
                .withIncome()
                .withExpenditure()
                .build();

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> clubService.createClub(incomingClubWithIncorrectManagerFunds, userId, USER_EMAIL));

        // assert
        verify(clubDAO, never()).insertEntity(any());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422, serviceException.getResponseStatus());
    }

    /**
     * given a club entity whose transfer and wage budgets is greater than the manager funds set, tests that no data is
     * persisted and a service exception is thrown
     */
    @Test
    public void createClubWithIncorrectBudget() {
        // setup
        Club incomingClubWithIncorrectBudget = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .customTransferBudget(new BigDecimal("5000"))
                .customWageBudget(new BigDecimal("2000"))
                .withIncome()
                .withExpenditure()
                .build();

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> clubService.createClub(incomingClubWithIncorrectBudget, userId, USER_EMAIL));

        // assert
        verify(clubDAO, never()).insertEntity(any());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422, serviceException.getResponseStatus());
    }

    /**
     * given a valid club entity and an identifier for an existing club entity, tests that the corresponding club
     * entity is updated with the incoming properties and persisted in the DAO layer
     */
    @Test
    public void updateClubUpdatesClubData() {
        // setup
        UUID existingClubId = UUID.randomUUID();
        Club existingClubData = ClubDataProvider.ClubBuilder.builder()
                .isExisting(true)
                .existingUserId(userId)
                .withId(existingClubId)
                .withIncome()
                .withExpenditure()
                .build();

        BigDecimal updatedWageBudget = existingClubData.getWageBudget().add(new BigDecimal("100"));
        BigDecimal updatedTransferBudget = existingClubData.getTransferBudget().add(new BigDecimal("100"));
        BigDecimal totalFunds = updatedTransferBudget.add(updatedWageBudget);
        Club incomingClubBase = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .withId(existingClubId)
                .build();
        Club incomingClub = ClubDataProvider.ModifiedClubBuilder.builder()
                .from(incomingClubBase)
                .withUpdatedTransferBudget(updatedTransferBudget)
                .withUpdatedWageBudget(updatedWageBudget)
                .withUpdatedManagerFunds(totalFunds)
                .build();

        ArgumentCaptor<Club> updatedClubCaptor = ArgumentCaptor.forClass(Club.class);

        // execute
        Club updatedClub = clubService.updateClub(incomingClub, existingClubData, existingClubId);

        // assert
        verify(clubDAO).updateEntity(eq(existingClubId), updatedClubCaptor.capture());
        assertEquals(updatedClub, updatedClubCaptor.getValue());

        assertEquals(existingClubData.getId(), updatedClub.getId());
        assertEquals(updatedTransferBudget, updatedClub.getTransferBudget());
        assertEquals(updatedWageBudget, updatedClub.getWageBudget());
        assertEquals(totalFunds, updatedClub.getManagerFunds().getCurrent());
        assertEquals(existingClubData.getManagerFunds().getHistory().size() + 1,
                updatedClub.getManagerFunds().getHistory().size());

        // assertions for general house-keeping fields
        assertEquals(USER_EMAIL, updatedClub.getCreatedBy());
        assertEquals(userId, updatedClub.getUserId());
        assertEquals(LocalDate.now(), updatedClub.getLastModifiedDate());
    }

    /**
     * given a valid club entity where only the transfer and wage budget split has changes, tests that the corresponding
     * club entity with the incoming properties and persisted in the DAO layer while the manager funds remain unchanged
     */
    @Test
    public void updateClubWhenTransferAndWageBudgetSplitChanges() {
        // setup
        UUID existingClubId = UUID.randomUUID();
        Club existingClubData = ClubDataProvider.ClubBuilder.builder()
                .isExisting(true)
                .existingUserId(userId)
                .withId(existingClubId)
                .withIncome()
                .withExpenditure()
                .build();

        // decrease transfer budget by 100 and increase wage budget by the same amount
        BigDecimal updatedTransferBudget = existingClubData.getTransferBudget()
                .subtract(new BigDecimal("100"));
        BigDecimal updatedWageBudget = existingClubData.getWageBudget().add(new BigDecimal("100"));
        Club incomingClubBase = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .withId(existingClubId)
                .build();

        Club incomingClub = ClubDataProvider.ModifiedClubBuilder.builder()
                .from(incomingClubBase)
                .withUpdatedTransferBudget(updatedTransferBudget)
                .withUpdatedWageBudget(updatedWageBudget)
                .build();

        ArgumentCaptor<Club> updatedClubCaptor = ArgumentCaptor.forClass(Club.class);

        // execute
        Club updatedClub = clubService.updateClub(incomingClub, existingClubData, existingClubId);

        // assert
        verify(clubDAO).updateEntity(eq(existingClubId), updatedClubCaptor.capture());
        assertEquals(updatedClub, updatedClubCaptor.getValue());

        assertEquals(updatedTransferBudget, updatedClub.getTransferBudget());
        assertEquals(updatedWageBudget, updatedClub.getWageBudget());
        assertEquals(existingClubData.getManagerFunds(), updatedClub.getManagerFunds());
    }

    /**
     * given a club entity whose manager funds is greater than the transfer and wage budgets set, tests that the
     * corresponding club data is not updated and a service exception is thrown instead
     */
    @Test
    public void updateClubWithIncorrectManagerFunds() {
        // setup
        UUID existingClubId = UUID.randomUUID();
        Club existingClubData = ClubDataProvider.ClubBuilder.builder()
                .isExisting(true)
                .existingUserId(userId)
                .withId(existingClubId)
                .withIncome()
                .withExpenditure()
                .build();

        Club incomingClubBase = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .withId(existingClubId)
                .build();
        BigDecimal updatedWageBudget = incomingClubBase.getWageBudget().add(new BigDecimal("100"));
        BigDecimal updatedTransferBudget = incomingClubBase.getTransferBudget().add(new BigDecimal("100"));
        Club incomingClub = ClubDataProvider.ModifiedClubBuilder.builder()
                .from(incomingClubBase)
                .withUpdatedWageBudget(updatedWageBudget)
                .withUpdatedWageBudget(updatedTransferBudget)
                .withUpdatedManagerFunds(new BigDecimal("100")) // set manager funds to an arbitrary value
                .build();

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> clubService.updateClub(incomingClub, existingClubData, existingClubId));

        // assert
        verify(clubDAO, never()).updateEntity(any(), any());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422, serviceException.getResponseStatus());
    }

    /**
     * given a club entity  whose transfer and wage budgets is greater than the manager funds set, tests that the
     * corresponding club data is not updated and a service exception is thrown instead
     */
    @Test
    public void updateClubWithIncorrectBudget() {
        // setup
        UUID existingClubId = UUID.randomUUID();
        Club existingClubData = ClubDataProvider.ClubBuilder.builder()
                .isExisting(true)
                .existingUserId(userId)
                .withId(existingClubId)
                .withIncome()
                .withExpenditure()
                .build();
        Club incomingClubBase = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .withId(existingClubId)
                .build();
        Club incomingClub = ClubDataProvider.ModifiedClubBuilder.builder()
                .from(incomingClubBase)
                // set wage and transfer budgets to arbitrary values which do not match the existing manager funds
                .withUpdatedWageBudget(new BigDecimal("5000"))
                .withUpdatedWageBudget(new BigDecimal("1000"))
                .build();

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> clubService.updateClub(incomingClub, existingClubData, existingClubId));

        // assert
        verify(clubDAO, never()).updateEntity(any(), any());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422, serviceException.getResponseStatus());
    }

    /**
     * given a club entity with updated income and expenditure data in the request, tests that they are ignored while
     * the rest of the club entity is updated with the incoming properties and persisted in the DAO layer
     */
    @Test
    public void updateClubIgnoresUpdatedIncomeAndExpenditure() {
        // setup
        String updatedClubName = "updated club name";
        UUID existingClubId = UUID.randomUUID();
        Club existingClubData = ClubDataProvider.ClubBuilder.builder()
                .isExisting(true)
                .existingUserId(userId)
                .withId(existingClubId)
                .withIncome()
                .withExpenditure()
                .build();
        when(clubDAO.getEntity(eq(existingClubId))).thenReturn(existingClubData);

        Club incomingClubBase = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .withId(existingClubId)
                .withIncome()
                .withExpenditure()
                .build();
        Club incomingClub = ClubDataProvider.ModifiedClubBuilder.builder()
                .from(incomingClubBase)
                .withUpdatedName(updatedClubName)
                .withUpdatedIncome()
                .withUpdatedExpenditure()
                .build();

        ArgumentCaptor<Club> updatedClubCaptor = ArgumentCaptor.forClass(Club.class);

        // execute
        Club updatedClub = clubService.updateClub(incomingClub, existingClubData, existingClubId);

        // assert
        verify(clubDAO).updateEntity(eq(existingClubId), updatedClubCaptor.capture());
        assertEquals(updatedClub, updatedClubCaptor.getValue());

        assertEquals(updatedClubName, updatedClub.getName());

        assertNotNull(existingClubData.getIncome());
        assertNotNull(updatedClub.getIncome());
        assertEquals(existingClubData.getIncome().getCurrent(), updatedClub.getIncome().getCurrent());
        assertEquals(existingClubData.getIncome().getHistory(), updatedClub.getIncome().getHistory());

        assertNotNull(existingClubData.getExpenditure());
        assertNotNull(updatedClub.getExpenditure());
        assertEquals(existingClubData.getExpenditure().getCurrent(), updatedClub.getExpenditure().getCurrent());
        assertEquals(existingClubData.getExpenditure().getHistory(), updatedClub.getExpenditure().getHistory());
    }

    /**
     * given a valid club ID, removes the club entity from the DAO layer
     */
    @Test
    public void deleteClubRemovesClubData() {
        // setup
        UUID clubId = UUID.randomUUID();
        Club existingClubData = ClubDataProvider.ClubBuilder.builder()
                .isExisting(true)
                .withId(clubId)
                .existingUserId(userId)
                .withIncome()
                .withExpenditure()
                .build();
        when(clubDAO.getEntity(eq(clubId))).thenReturn(existingClubData);

        // execute
        clubService.deleteClub(clubId, userId);

        // assert
        verify(clubDAO).getEntity(any());
        verify(clubDAO).deleteEntity(eq(clubId));
    }

    /**
     * given an ID to a club not belong to a user, tests that the club data is not deleted and a service exception is
     * thrown instead
     */
    @Test
    public void deleteClubWhenClubDoesNotBelongToUser() {
        // setup
        UUID existingClubId = UUID.randomUUID();
        Club existingClubData = ClubDataProvider.ClubBuilder.builder()
                .isExisting(true)
                .withId(existingClubId)
                .existingUserId(UUID.randomUUID())
                .withIncome()
                .withExpenditure()
                .build();
        when(clubDAO.getEntity(eq(existingClubId))).thenReturn(existingClubData);

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> clubService.deleteClub(existingClubId, userId));

        // assert
        verify(clubDAO).getEntity(any());
        verify(clubDAO, never()).deleteEntity(any());
        assertEquals(HttpStatus.FORBIDDEN_403, serviceException.getResponseStatus());
    }

    /**
     * given an invalid club id, tests that the EntityNotFound exception thrown by the DAO layer is handled and
     * ServiceException is thrown instead
     */
    @Test
    public void deleteClubWhenClubDataDoesNotExist() {
        // setup
        UUID invalidClubId = UUID.randomUUID();
        when(clubDAO.getEntity(eq(invalidClubId))).thenThrow(EntityNotFoundException.class);

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> clubService.deleteClub(invalidClubId, userId));

        // assert
        verify(clubDAO, never()).deleteEntity(any());
        assertEquals(HttpStatus.NOT_FOUND_404, serviceException.getResponseStatus());
    }
    /**
     * given a valid user entity as the auth principal, tests that all clubs associated with the user is fetched from
     * the DAO layer
     */
    @Test
    public void getClubsByUserIdFetchesAllClubsForUser() {
        // setup
        List<ClubSummary> mockClubData = ClubDataProvider.getAllClubSummariesForUser(userId);
        when(clubDAO.getClubSummariesForUser(eq(userId))).thenReturn(mockClubData);

        // execute
        List<ClubSummary> clubSummaries = clubService.getClubSummariesByUserId(userId);

        // assert
        verify(clubDAO).getClubSummariesForUser(any());
        assertFalse(clubSummaries.isEmpty());

        for (int idx = 0; idx < clubSummaries.size(); idx++) {
            assertTrue(clubSummaries.get(idx).getName().contains(String.valueOf(idx)));
        }
    }

    /**
     * given a valid user entity as the auth principal but no there are no club entities associated with it, tests that
     * an empty list is returned
     */
    @Test
    public void getClubsByUserIdWhenNoClubsAreAssociatedWithUser() {
        // setup
        when(clubDAO.getClubSummariesForUser(eq(userId))).thenReturn(new ArrayList<>());

        // execute
        List<ClubSummary> clubList = clubService.getClubSummariesByUserId(userId);

        // assert
        verify(clubDAO).getClubSummariesForUser(any());
        assertTrue(clubList.isEmpty());
    }

    /**
     * given a valid club ID, fetches player data for all players associated with the club from the DAO layer
     */
    @Test
    public void getSquadPlayersFetchesPlayerDataAssociatedWithClub() {
        // setup
        UUID clubId = UUID.randomUUID();
        ImmutableSquadPlayer expectedSquadPlayer = ImmutableSquadPlayer.builder()
                .name("fake player name")
                .country("fake player country")
                .countryFlag("fake country flag url")
                .role("fake player role")
                .currentAbility(CURRENT_PLAYER_ABILITY)
                .recentForm(new ArrayList<>())
                .playerId(UUID.randomUUID())
                .build();
        when(clubDAO.getPlayersInClub(eq(clubId))).thenReturn(ImmutableList.of(expectedSquadPlayer));

        // execute
        List<SquadPlayer> squadPlayerList = clubService.getSquadPlayers(clubId);

        // assert
        verify(clubDAO).getPlayersInClub(eq(clubId));

        assertFalse(squadPlayerList.isEmpty());
        squadPlayerList.forEach(squadPlayer -> {
            assertNotNull(squadPlayer);
            assertEquals(expectedSquadPlayer, squadPlayer);
        });
    }
}