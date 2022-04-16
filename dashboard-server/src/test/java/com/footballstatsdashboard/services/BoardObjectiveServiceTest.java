package com.footballstatsdashboard.services;

import com.footballstatsdashboard.BoardObjectiveDataProvider;
import com.footballstatsdashboard.api.model.club.BoardObjective;
import com.footballstatsdashboard.core.exceptions.ServiceException;
import com.footballstatsdashboard.db.IBoardObjectiveEntityDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

public class BoardObjectiveServiceTest {
    private static final String USER_EMAIL = "fake email";

    private BoardObjectiveService boardObjectiveService;

    @Mock
    IBoardObjectiveEntityDAO boardObjectiveDAO;

    @Before
    public void initialize() {
        MockitoAnnotations.openMocks(this);

        boardObjectiveService = new BoardObjectiveService(boardObjectiveDAO);
    }

    /**
     * given a valid board objective id, tests that the board objective entity is successfully fetched and returned
     */
    @Test
    public void getBoardObjectiveFetchesBoardObjectiveData() {
        // setup
        UUID boardObjectiveId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();
        BoardObjective boardObjectiveData = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder()
                .withExistingId(boardObjectiveId)
                .isExisting(true)
                .withClubId(clubId)
                .build();
        when(boardObjectiveDAO.getEntity(eq(boardObjectiveId))).thenReturn(boardObjectiveData);

        // execute
        BoardObjective boardObjective = boardObjectiveService.getBoardObjective(boardObjectiveId, clubId);

        // assert
        verify(boardObjectiveDAO).getEntity(any());
        assertEquals(boardObjectiveId, boardObjective.getId());
    }

    /**
     * given an invalid board objective id, tests that the EntityNotFound exception thrown from the DAO layer is
     * handled and a service exception is thrown
     */
    @Test(expected = ServiceException.class)
    public void getBoardObjectiveWhenBoardObjectiveDataCannotBeFound() {
        // setup
        UUID nonExistentBoardObjectiveId = UUID.randomUUID();
        when(boardObjectiveDAO.getEntity(eq(nonExistentBoardObjectiveId))).thenThrow(EntityNotFoundException.class);

        // execute
        boardObjectiveService.getBoardObjective(nonExistentBoardObjectiveId, UUID.randomUUID());

        // assert
        verify(boardObjectiveDAO).getEntity(any());
    }

    /**
     * given an invalid board objective id, tests that if the board objective does not belong to the club, no board
     * objective data is returned and a service exception is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void getBoardObjectiveWhenBoardObjectiveDoesNotBelongToClub() {
        // setup
        UUID inaccessibleBoardObjectiveId = UUID.randomUUID();
        UUID inaccessibleClubId = UUID.randomUUID();
        UUID accessibleClubId = UUID.randomUUID();
        BoardObjective inaccessibleBoardObjective = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder()
                .isExisting(true)
                .withExistingId(inaccessibleBoardObjectiveId)
                .withClubId(inaccessibleClubId)
                .build();
        when(boardObjectiveDAO.getEntity(eq(inaccessibleBoardObjectiveId))).thenReturn(inaccessibleBoardObjective);

        // execute
        boardObjectiveService.getBoardObjective(inaccessibleBoardObjectiveId, accessibleClubId);

        // assert
        verify(boardObjectiveDAO).getEntity(any());
    }

    /**
     * given a valid board objective entity, tests that the data is persisted correctly
     */
    @Test
    public void createBoardObjectivePersistsBoardObjectiveData() {
        // setup
        UUID clubId = UUID.randomUUID();
        BoardObjective incomingBoardObjective = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder().build();
        ArgumentCaptor<BoardObjective> newBoardObjectiveCaptor = ArgumentCaptor.forClass(BoardObjective.class);

        // execute
        BoardObjective createdBoardObjective = boardObjectiveService.createBoardObjective(incomingBoardObjective,
                clubId, USER_EMAIL);

        // assert
        verify(boardObjectiveDAO).insertEntity(newBoardObjectiveCaptor.capture());
        BoardObjective newBoardObjective = newBoardObjectiveCaptor.getValue();
        assertEquals(createdBoardObjective, newBoardObjective);

        // assertions for general house-keeping fields
        assertNotNull(createdBoardObjective.getCreatedDate());
        assertNotNull(createdBoardObjective.getLastModifiedDate());
        assertEquals(USER_EMAIL, createdBoardObjective.getCreatedBy());
        assertEquals(clubId, newBoardObjective.getClubId());
    }

    /**
     * given a valid board objective entity and an identifier for an existing board objective, tests that the
     * corresponding board objective data is updated in persistent storage
     */
    @Test
    public void updateBoardObjectiveUpdateBoardObjectiveData() {
        // setup
        UUID existingClubId = UUID.randomUUID();
        UUID existingBoardObjectiveId = UUID.randomUUID();
        BoardObjective existingBoardObjective = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder()
                .withExistingId(existingBoardObjectiveId)
                .isExisting(true)
                .withClubId(existingClubId)
                .build();
        when(boardObjectiveDAO.getEntity(eq(existingBoardObjectiveId))).thenReturn(existingBoardObjective);
        ArgumentCaptor<BoardObjective> updatedBoardObjectiveCaptor = ArgumentCaptor.forClass(BoardObjective.class);
        BoardObjective incomingBoardObjective = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder()
                .customTitle("updated fake objective title")
                .customDescription("updated fake objective description.")
                .toggleCompletionStatus()
                .build();

        // execute
        BoardObjective updatedBoardObjective = boardObjectiveService.updateBoardObjective(existingBoardObjectiveId,
                existingClubId, incomingBoardObjective);

        // assert
        verify(boardObjectiveDAO).getEntity(any());
        verify(boardObjectiveDAO).updateEntity(eq(existingBoardObjectiveId), updatedBoardObjectiveCaptor.capture());
        BoardObjective capturedBoardObjective = updatedBoardObjectiveCaptor.getValue();
        assertEquals(updatedBoardObjective, capturedBoardObjective);

        assertEquals(existingBoardObjectiveId, capturedBoardObjective.getId());
        assertEquals(existingClubId, capturedBoardObjective.getClubId());
        assertEquals(incomingBoardObjective.getTitle(), capturedBoardObjective.getTitle());
        assertEquals(incomingBoardObjective.getDescription(), capturedBoardObjective.getDescription());
        assertEquals(incomingBoardObjective.isCompleted(), capturedBoardObjective.isCompleted());

        assertNotNull(capturedBoardObjective.getLastModifiedDate());
        assertTrue(capturedBoardObjective.getLastModifiedDate().isAfter(Objects.requireNonNull(existingBoardObjective.getLastModifiedDate())));
    }

    /**
     * given a valid board objective entity and an identifier for an existing board objective, tests that if the board
     * objective for that identifier does not exist, no board objective data is updated and a service exception is
     * thrown instead
     */
    @Test(expected = ServiceException.class)
    public void updateBoardObjectiveWhenBoardObjectiveDoesNotExist() {
        // setup
        UUID nonExistentBoardObjectiveId = UUID.randomUUID();
        when(boardObjectiveDAO.getEntity(eq(nonExistentBoardObjectiveId))).thenThrow(EntityNotFoundException.class);

        BoardObjective incomingBoardObjective = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder()
                .customTitle("updated fake objective title")
                .customDescription("updated fake objective description.")
                .toggleCompletionStatus()
                .build();

        // execute
        boardObjectiveService.updateBoardObjective(nonExistentBoardObjectiveId, UUID.randomUUID(),
                incomingBoardObjective);

        // assert
        verify(boardObjectiveDAO).getEntity(any());
        verify(boardObjectiveDAO, never()).updateEntity(any(), any());
    }

    /**
     * given an invalid board objective id, tests that if the board objective does not belong to the club, no board
     * objective data is updated and a service exception is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void updateBoardObjectiveWhenBoardObjectiveDoesNotBelongToClub() {
        // setup
        UUID inaccessibleBoardObjectiveId = UUID.randomUUID();
        UUID inaccessibleClubId = UUID.randomUUID();
        UUID accessibleClubId = UUID.randomUUID();
        BoardObjective inaccessibleBoardObjective = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder()
                .isExisting(true)
                .withExistingId(inaccessibleBoardObjectiveId)
                .withClubId(inaccessibleClubId)
                .build();
        when(boardObjectiveDAO.getEntity(eq(inaccessibleBoardObjectiveId))).thenReturn(inaccessibleBoardObjective);

        BoardObjective incomingBoardObjective = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder()
                .customTitle("updated fake objective title")
                .customDescription("updated fake objective description.")
                .toggleCompletionStatus()
                .build();

        // execute
        boardObjectiveService.updateBoardObjective(inaccessibleBoardObjectiveId, accessibleClubId,
                incomingBoardObjective);

        // assert
        verify(boardObjectiveDAO).getEntity(any());
        verify(boardObjectiveDAO, never()).updateEntity(any(), any());
    }

    /**
     * given a valid board objective id, removes the board objective entity from persistent storage
     */
    @Test
    public void deleteBoardObjectiveRemovesBoardObjectiveData() {
        // setup
        UUID boardObjectiveId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();
        BoardObjective existingBoardObjective = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder()
                .isExisting(true)
                .withExistingId(boardObjectiveId)
                .withClubId(clubId)
                .build();
        when(boardObjectiveDAO.getEntity(eq(boardObjectiveId))).thenReturn(existingBoardObjective);

        // execute
        boardObjectiveService.deleteBoardObjective(boardObjectiveId, clubId);

        // assert
        verify(boardObjectiveDAO).getEntity(any());
        verify(boardObjectiveDAO).deleteEntity(eq(boardObjectiveId));
    }

    /**
     * given an invalid board objective id, tests that the EntityNotFound exception thrown by the DAO layer is handled
     * and a ServiceException is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void deleteBoardObjectiveWhenBoardObjectiveDataCannotBeNotFound() {
        // setup
        UUID nonExistentBoardObjectiveId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();
        when(boardObjectiveDAO.getEntity(eq(nonExistentBoardObjectiveId))).thenThrow(EntityNotFoundException.class);

        // execute
        boardObjectiveService.deleteBoardObjective(nonExistentBoardObjectiveId, clubId);

        // assert
        verify(boardObjectiveDAO).getEntity(any());
        verify(boardObjectiveDAO, never()).deleteEntity(any());
    }

    /**
     * given an invalid board objective id, tests that if the board objective does not belong to the club, no board
     * objective data is deleted and a service exception is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void deleteBoardObjectiveWhenBoardObjectiveDoesNotBelongToClub() {
        // setup
        UUID inaccessibleBoardObjectiveId = UUID.randomUUID();
        UUID inaccessibleClubId = UUID.randomUUID();
        UUID accessibleClubId = UUID.randomUUID();
        BoardObjective inaccessibleBoardObjective = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder()
                .isExisting(true)
                .withExistingId(inaccessibleBoardObjectiveId)
                .withClubId(inaccessibleClubId)
                .build();
        when(boardObjectiveDAO.getEntity(eq(inaccessibleBoardObjectiveId))).thenReturn(inaccessibleBoardObjective);

        // execute
        boardObjectiveService.deleteBoardObjective(inaccessibleBoardObjectiveId, accessibleClubId);

        // assert
        verify(boardObjectiveDAO).getEntity(any());
        verify(boardObjectiveDAO, never()).deleteEntity(any());
    }

    /**
     * given a valid club id, tests that all the board objective data associated with it are fetched from persistent
     * storage and returned
     */
    @Test
    public void getAllBoardObjectivesForClubFetchesAllBoardObjectivesAssociatedWithClub() {
        // setup
        UUID existingClubId = UUID.randomUUID();
        List<BoardObjective> boardObjectivesAssociatedWithClub = getMultipleBoardObjectives(existingClubId);
        when(boardObjectiveDAO.getBoardObjectivesForClub(eq(existingClubId)))
                .thenReturn(boardObjectivesAssociatedWithClub);

        // execute
        List<BoardObjective> boardObjectives = boardObjectiveService.getAllBoardObjectivesForClub(existingClubId);

        // assert
        verify(boardObjectiveDAO).getBoardObjectivesForClub(any());
        assertFalse(boardObjectives.isEmpty());
        assertEquals(boardObjectivesAssociatedWithClub, boardObjectives);
    }

    private List<BoardObjective> getMultipleBoardObjectives(UUID clubId) {
        return IntStream.range(0, 5)
                .mapToObj(i ->
                        BoardObjectiveDataProvider.BoardObjectiveBuilder.builder()
                                .isExisting(true)
                                .withClubId(clubId)
                                .customTitle("fake objective title " + i)
                                .customDescription("fake objective description " + i)
                                .build()
                ).collect(Collectors.toList());
    }
}