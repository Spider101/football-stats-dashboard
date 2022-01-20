package com.footballstatsdashboard.services;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.footballstatsdashboard.ClubDataProvider;
import com.footballstatsdashboard.api.model.Club;
import com.footballstatsdashboard.api.model.club.ClubSummary;
import com.footballstatsdashboard.api.model.club.ImmutableSquadPlayer;
import com.footballstatsdashboard.api.model.club.SquadPlayer;
import com.footballstatsdashboard.core.exceptions.ServiceException;
import com.footballstatsdashboard.db.ClubDAO;
import com.footballstatsdashboard.db.key.ResourceKey;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClubServiceTest {
    private static final String USER_EMAIL = "fake email";
    private static final int CURRENT_PLAYER_ABILITY = 19;

    private UUID userId;
    private ClubService clubService;

    @Mock
    private ClubDAO<ResourceKey> clubDAO;

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
     * given a valid club id, tests that the club entity is successfully fetched from couchbase server and returned
     * in the response
     */
    @Test
    public void getClubFetchesClubFromCouchbase() {
        // setup
        UUID clubId = UUID.randomUUID();
        Club clubFromCouchbase = ClubDataProvider.ClubBuilder.builder()
                .isExisting(true)
                .existingUserId(userId)
                .withId(clubId)
                .withIncome()
                .withExpenditure()
                .build();
        when(clubDAO.getDocument(any(), any())).thenReturn(clubFromCouchbase);

        // execute
        Club club = clubService.getClub(clubId);

        // assert
        verify(clubDAO).getDocument(any(), any());

        assertEquals(clubId, club.getId());
        assertNotNull(club.getIncome());
        assertNotNull(club.getExpenditure());
        assertNotNull(club.getUserId());
        assertEquals(userId, club.getUserId());
    }

    /**
     * given an invalid club id, tests that the DocumentNotFound exception thrown by the DAO layer is handled and a
     * ServiceException is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void getClubWhenClubNotFoundInCouchbase() {
        // setup
        UUID invalidClubId = UUID.randomUUID();
        when(clubDAO.getDocument(any(), any())).thenThrow(DocumentNotFoundException.class);

        // execute
        clubService.getClub(invalidClubId);

        // assert
        verify(clubDAO).getDocument(any(), any());
    }

    /**
     * given a valid club entity in the request, tests that the internal fields are set correctly on the entity and
     * persisted in couchbase
     */
    @Test
    public void createClubPersistsClubInCouchbase() {
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
        verify(clubDAO).insertDocument(any(), newClubCaptor.capture());
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
     * given that the request contains a club entity with an empty club name, tests that no data is persisted in
     * couchbase and a service exception is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void createClubWhenClubNameIsEmpty() {
        // setup
        Club incomingClubWithNoName = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .customClubName("")
                .withIncome()
                .withExpenditure()
                .build();

        // execute
        clubService.createClub(incomingClubWithNoName, userId, USER_EMAIL);

        // assert
        verify(clubDAO, never()).insertDocument(any(), any());
    }

    /**
     * given that the request contains a club entity without valid income data, tests that no data is persisted in
     * couchbase and a service exception is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void createClubWithoutIncomeData() {
        // setup
        Club incomingClubWithNoIncomeData = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .withExpenditure()
                .build();

        // execute
        clubService.createClub(incomingClubWithNoIncomeData, userId, USER_EMAIL);

        // assert
        verify(clubDAO, never()).insertDocument(any(), any());
    }

    /**
     * given that the request contains a club entity without valid expenditure data, tests that no data is persisted in
     * couchbase and a service exception is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void createClubWithoutExpenditureData() {
        // setup
        Club incomingClubWithNoExpenditureData = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .withIncome()
                .build();

        // execute
        clubService.createClub(incomingClubWithNoExpenditureData, userId, USER_EMAIL);

        // assert
        verify(clubDAO, never()).insertDocument(any(), any());
    }

    /**
     * given that the request contains a club entity whose manager funds is greater than the transfer and wage budgets
     * set, tests that no data is persisted in couchbase and a service exception is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void createClubWithIncorrectManagerFunds() {
        // setup
        Club incomingClubWithIncorrectManagerFunds = ClubDataProvider.ClubBuilder.builder()
                .isExisting(false)
                .customManagerFunds(new BigDecimal("10000"))
                .withIncome()
                .withExpenditure()
                .build();

        // execute
        clubService.createClub(incomingClubWithIncorrectManagerFunds, userId, USER_EMAIL);

        // assert
        verify(clubDAO, never()).insertDocument(any(), any());
    }

    /**
     * given that the request contains a club entity whose transfer and wage budgets is greater than the manager funds
     * set, tests that no data is persisted in couchbase and a service exception is thrown
     */
    @Test(expected = ServiceException.class)
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
        clubService.createClub(incomingClubWithIncorrectBudget, userId, USER_EMAIL);

        // assert
        verify(clubDAO, never()).insertDocument(any(), any());
    }

    /**
     * given a valid club entity in the request, tests that an updated club entity with update internal fields is
     * upserted in couchbase
     */
    @Test
    public void updateClubUpdatesClubInCouchbase() {
        // setup
        UUID existingClubId = UUID.randomUUID();
        Club existingClub = ClubDataProvider.ClubBuilder.builder()
                .isExisting(true)
                .existingUserId(userId)
                .withId(existingClubId)
                .withIncome()
                .withExpenditure()
                .build();

        BigDecimal updatedWageBudget = existingClub.getWageBudget().add(new BigDecimal("100"));
        BigDecimal updatedTransferBudget = existingClub.getTransferBudget().add(new BigDecimal("100"));
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
        Club updatedClub = clubService.updateClub(incomingClub, existingClub, existingClubId);

        // assert
        verify(clubDAO).updateDocument(any(), updatedClubCaptor.capture());
        Club clubToBeUpdatedInCouchbase = updatedClubCaptor.getValue();
        assertEquals(updatedClub, clubToBeUpdatedInCouchbase);

        assertEquals(existingClub.getId(), updatedClub.getId());
        assertEquals(updatedTransferBudget, updatedClub.getTransferBudget());
        assertEquals(updatedWageBudget, updatedClub.getWageBudget());
        assertEquals(totalFunds, updatedClub.getManagerFunds().getCurrent());
        assertEquals(existingClub.getManagerFunds().getHistory().size() + 1,
                updatedClub.getManagerFunds().getHistory().size());

        // assertions for general house-keeping fields
        assertEquals(USER_EMAIL, updatedClub.getCreatedBy());
        assertEquals(userId, updatedClub.getUserId());
        assertEquals(LocalDate.now(), updatedClub.getLastModifiedDate());
    }

    /**
     * given a valid club entity where only the transfer and wage budget split has changes, tests that the manager funds
     * entity is left unchanged when the club data is upserted in couchbase
     */
    @Test
    public void updateClubWhenTransferAndWageBudgetSplitChanges() {
        // setup
        UUID existingClubId = UUID.randomUUID();
        Club existingClub = ClubDataProvider.ClubBuilder.builder()
                .isExisting(true)
                .existingUserId(userId)
                .withId(existingClubId)
                .withIncome()
                .withExpenditure()
                .build();

        // decrease transfer budget by 100 and increase wage budget by the same amount
        BigDecimal updatedTransferBudget = existingClub.getTransferBudget()
                .subtract(new BigDecimal("100"));
        BigDecimal updatedWageBudget = existingClub.getWageBudget().add(new BigDecimal("100"));
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
        Club updatedClub = clubService.updateClub(incomingClub, existingClub, existingClubId);

        // assert
        verify(clubDAO).updateDocument(any(), updatedClubCaptor.capture());
        Club clubToBeUpdatedInCouchbase = updatedClubCaptor.getValue();
        assertEquals(updatedClub, clubToBeUpdatedInCouchbase);

        assertEquals(updatedTransferBudget, updatedClub.getTransferBudget());
        assertEquals(updatedWageBudget, updatedClub.getWageBudget());
        assertEquals(existingClub.getManagerFunds(), updatedClub.getManagerFunds());
    }

    /**
     * given that the request contains a club entity whose manager funds is greater than the transfer and wage budgets
     * set, tests that no data is updated in couchbase and a service exception is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void updateClubWithIncorrectManagerFunds() {
        // setup
        UUID existingClubId = UUID.randomUUID();
        Club existingClub = ClubDataProvider.ClubBuilder.builder()
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
        clubService.updateClub(incomingClub, existingClub, existingClubId);

        // assert
        verify(clubDAO, never()).updateDocument(any(), any());
    }

    /**
     * given that the request contains a club entity  whose transfer and wage budgets is greater than the manager funds
     * set, tests that no data is updated in couchbase and a service exception is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void updateClubWithIncorrectBudget() {
        // setup
        UUID existingClubId = UUID.randomUUID();
        Club existingClub = ClubDataProvider.ClubBuilder.builder()
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
        clubService.updateClub(incomingClub, existingClub, existingClubId);

        // assert
        verify(clubDAO, never()).updateDocument(any(), any());
    }

    /**
     * given a club entity with updated income and expenditure data in the request, tests that they are ignored while
     * the rest of the club entity if updated and upserted in couchbase
     */
    @Test
    public void updateClubIgnoresUpdatedIncomeAndExpenditure() {
        // setup
        String updatedClubName = "updated club name";
        UUID existingClubId = UUID.randomUUID();
        Club existingClubInCouchbase = ClubDataProvider.ClubBuilder.builder()
                .isExisting(true)
                .existingUserId(userId)
                .withId(existingClubId)
                .withIncome()
                .withExpenditure()
                .build();
        when(clubDAO.getDocument(any(), any())).thenReturn(existingClubInCouchbase);

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
        Club updatedClub = clubService.updateClub(incomingClub, existingClubInCouchbase, existingClubId);

        // assert
        verify(clubDAO).updateDocument(any(), updatedClubCaptor.capture());
        Club clubToBeUpdatedInCouchbase = updatedClubCaptor.getValue();
        assertEquals(updatedClub, clubToBeUpdatedInCouchbase);

        assertEquals(updatedClubName, updatedClub.getName());

        assertNotNull(existingClubInCouchbase.getIncome());
        assertNotNull(updatedClub.getIncome());
        assertEquals(existingClubInCouchbase.getIncome().getCurrent(), updatedClub.getIncome().getCurrent());
        assertEquals(existingClubInCouchbase.getIncome().getHistory(), updatedClub.getIncome().getHistory());

        assertNotNull(existingClubInCouchbase.getExpenditure());
        assertNotNull(updatedClub.getExpenditure());
        assertEquals(existingClubInCouchbase.getExpenditure().getCurrent(), updatedClub.getExpenditure().getCurrent());
        assertEquals(existingClubInCouchbase.getExpenditure().getHistory(), updatedClub.getExpenditure().getHistory());
    }

    /**
     * given a valid club ID, removes the club entity from couchbase
     */
    @Test
    public void deleteClubRemovesClubFromCouchbase() {
        // setup
        UUID clubId = UUID.randomUUID();
        ArgumentCaptor<ResourceKey> resourceKeyCaptor = ArgumentCaptor.forClass(ResourceKey.class);

        // execute
        clubService.deleteClub(clubId);

        // assert
        verify(clubDAO).deleteDocument(resourceKeyCaptor.capture());
        ResourceKey capturedResourceKey = resourceKeyCaptor.getValue();
        assertEquals(clubId, capturedResourceKey.getResourceId());
    }

    /**
     * given an invalid club id, tests that the DocumentNotFound exception thrown by the DAO layer is handled and a
     * ServiceException is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void deleteClubWhenClubNotFoundInCouchbase() {
        // setup
        UUID invalidClubId = UUID.randomUUID();
        ArgumentCaptor<ResourceKey> resourceKeyCaptor = ArgumentCaptor.forClass(ResourceKey.class);
        doThrow(DocumentNotFoundException.class).when(clubDAO).deleteDocument(any());

        // execute
        clubService.deleteClub(invalidClubId);

        // assert
        verify(clubDAO).deleteDocument(resourceKeyCaptor.capture());
        assertEquals(invalidClubId, resourceKeyCaptor.getValue().getResourceId());
    }

    /**
     * given a valid user entity as the auth principal, tests that all clubs associated with the user is fetched from
     * couchbase
     */
    @Test
    public void getClubsByUserIdFetchesAllClubsForUser() {
        // setup
        List<ClubSummary> mockClubData = ClubDataProvider.getAllClubSummariesForUser(userId);
        when(clubDAO.getClubSummariesByUserId(any())).thenReturn(mockClubData);

        // execute
        List<ClubSummary> clubSummaries = clubService.getClubSummariesByUserId(userId);

        // assert
        verify(clubDAO).getClubSummariesByUserId(eq(userId));
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
    public void getClubsByUserIdReturnsEmptyListWhenNoClubsAreAssociatedWithUser() {
        // setup
        when(clubDAO.getClubSummariesByUserId(any())).thenReturn(new ArrayList<>());

        // execute
        List<ClubSummary> clubList = clubService.getClubSummariesByUserId(userId);

        // assert
        verify(clubDAO).getClubSummariesByUserId(eq(userId));
        assertTrue(clubList.isEmpty());
    }

    /**
     * given a valid club ID, fetches player data for all players associated with the club from couchbase
     */
    @Test
    public void getSquadPlayersFetchesPlayersFromCouchbase() {
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