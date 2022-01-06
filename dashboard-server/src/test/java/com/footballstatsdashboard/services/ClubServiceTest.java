package com.footballstatsdashboard.services;

import com.footballstatsdashboard.api.model.club.Club;
import com.footballstatsdashboard.api.model.club.ImmutableClub;
import com.footballstatsdashboard.api.model.club.ImmutableSquadPlayer;
import com.footballstatsdashboard.api.model.club.SquadPlayer;
import com.footballstatsdashboard.db.ClubDAO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
        Club clubFromCouchbase = getClubDataStub(clubId, userId, false, null);
        when(clubDAO.getDocument(any(), any())).thenReturn(clubFromCouchbase);

        // execute
        Club club = clubService.getClub(clubId);

        // assert
        verify(clubDAO).getDocument(any(), any());

        assertEquals(clubId, club.getId());
        assertNotNull(club.getUserId());
        assertEquals(userId, club.getUserId());
    }

    /**
     * given a runtime exception is thrown by couchbase DAO when club entity is not found, verifies that the same
     * exception is thrown by `getClub` resource method as well
     */
    @Test(expected = RuntimeException.class)
    public void getClubWhenClubNotFoundInCouchbase() {
        // setup
        UUID invalidClubId = UUID.randomUUID();
        when(clubDAO.getDocument(any(), any()))
                .thenThrow(new RuntimeException("Unable to find document with ID: " + invalidClubId));

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
        Club incomingClub = getClubDataStub(null, null, false, null);
        ArgumentCaptor<Club> newClubCaptor = ArgumentCaptor.forClass(Club.class);

        // execute
        Club createdClub = clubService.createClub(incomingClub, userId, USER_EMAIL);

        // assert
        verify(clubDAO).insertDocument(any(), newClubCaptor.capture());
        Club newClub = newClubCaptor.getValue();
        assertEquals(createdClub, newClub);

        assertEquals(incomingClub.getId(), createdClub.getId());

        // assertions for general house-keeping fields
        assertNotNull(createdClub.getCreatedDate());
        assertNotNull(createdClub.getLastModifiedDate());
        assertEquals(USER_EMAIL, createdClub.getCreatedBy());
        assertEquals(userId, createdClub.getUserId());
    }

    /**
     * given a valid club entity in the request, tests that an updated club entity with update internal fields is
     * upserted in couchbase
     */
    @Test
    public void updateClubUpdatesClubInCouchbase() {
        // setup
        UUID existingClubId = UUID.randomUUID();
        Club existingClubInCouchbase = getClubDataStub(existingClubId, userId, true, null);

        BigDecimal updatedWageBudget = existingClubInCouchbase.getWageBudget().add(new BigDecimal("100"));
        Club incomingClub = ImmutableClub.builder()
                .from(getClubDataStub(existingClubId, null, false, null))
                .wageBudget(updatedWageBudget)
                .build();
        when(clubDAO.getDocument(any(), any())).thenReturn(existingClubInCouchbase);

        ArgumentCaptor<Club> updatedClubCaptor = ArgumentCaptor.forClass(Club.class);

        // execute
        Club updatedClub = clubService.updateClub(incomingClub, existingClubInCouchbase, existingClubId);

        // assert
        verify(clubDAO).updateDocument(any(), updatedClubCaptor.capture());
        Club clubToBeUpdatedInCouchbase = updatedClubCaptor.getValue();
        assertEquals(updatedClub, clubToBeUpdatedInCouchbase);

        assertEquals(existingClubInCouchbase.getId(), updatedClub.getId());
        assertEquals(updatedWageBudget, updatedClub.getWageBudget());

        // assertions for general house-keeping fields
        assertEquals(USER_EMAIL, updatedClub.getCreatedBy());
        assertEquals(userId, updatedClub.getUserId());
        assertEquals(LocalDate.now(), updatedClub.getLastModifiedDate());
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
     * given a valid user entity as the auth principal, tests that all clubs associated with the user is fetched from
     * couchbase
     */
    @Test
    public void getClubsByUserIdFetchesAllClubsForUser() {
        // setup
        int numberOfClubs = 2;
        List<Club> mockClubData = IntStream.range(0, numberOfClubs).mapToObj(idx ->
                getClubDataStub(null, userId, true, "fake club name " + idx)
        ).collect(Collectors.toList());
        when(clubDAO.getClubsByUserId(any())).thenReturn(mockClubData);

        // execute
        List<Club> clubList = clubService.getClubsByUserId(userId);

        // assert
        verify(clubDAO).getClubsByUserId(eq(userId));
        assertFalse(clubList.isEmpty());

        for (int idx = 0; idx < clubList.size(); idx++) {
            assertEquals(userId, clubList.get(idx).getUserId());
            assertTrue(clubList.get(idx).getName().contains(String.valueOf(idx)));
        }
    }

    /**
     * given a valid user entity as the auth principal but no there are no club entities associated with it, tests that
     * an empty list is returned
     */
    @Test
    public void getClubsByUserIdReturnsEmptyListWhenNoClubsAreAssociatedWithUser() {
        // setup
        List<Club> mockClubData = new ArrayList<>();
        when(clubDAO.getClubsByUserId(any())).thenReturn(mockClubData);

        // execute
        List<Club> clubList = clubService.getClubsByUserId(userId);

        // assert
        verify(clubDAO).getClubsByUserId(eq(userId));
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

    private Club getClubDataStub(UUID clubId, UUID userID, boolean isExisting, String clubName) {
        ImmutableClub.Builder clubBuilder = ImmutableClub.builder()
                .name(clubName != null ? clubName : "fake club name")
                .expenditure(new BigDecimal("1000"))
                .income(new BigDecimal("2000"))
                .transferBudget(new BigDecimal("500"))
                .wageBudget(new BigDecimal("200"));

        if (clubId != null) clubBuilder.id(clubId);

        if (userID != null) {
            clubBuilder.userId(userID);
            clubBuilder.createdBy(USER_EMAIL);
        }

        if (isExisting) {
            Instant currentInstant = Instant.now();
            Instant olderInstant = currentInstant.minus(1, ChronoUnit.DAYS);
            clubBuilder.lastModifiedDate(LocalDate.ofInstant(olderInstant, ZoneId.systemDefault()));
        }

        return clubBuilder.build();
    }
}