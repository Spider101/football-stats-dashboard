package com.footballstatsdashboard.services;

import com.footballstatsdashboard.BoardObjectiveDataProvider;
import com.footballstatsdashboard.api.model.club.BoardObjective;
import com.footballstatsdashboard.core.exceptions.ServiceException;
import com.footballstatsdashboard.db.IBoardObjectiveEntityDAO;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Objects;
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

public class BoardObjectiveServiceTest {
    private static final String USER_EMAIL = "fake email";

    private UUID userId;
    private BoardObjectiveService boardObjectiveService;

    @Mock
    private IBoardObjectiveEntityDAO boardObjectiveDAO;

    @Before
    public void initialize() {
        MockitoAnnotations.openMocks(this);

        userId = UUID.randomUUID();
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
        when(boardObjectiveDAO.doesEntityBelongToClub(eq(boardObjectiveId), eq(clubId))).thenReturn(true);
        when(boardObjectiveDAO.doesEntityBelongToUser(eq(boardObjectiveId), eq(userId))).thenReturn(true);
        BoardObjective boardObjectiveData = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder()
                .withExistingId(boardObjectiveId)
                .isExisting(true)
                .withClubId(clubId)
                .build();
        when(boardObjectiveDAO.getEntity(eq(boardObjectiveId))).thenReturn(boardObjectiveData);

        // execute
        BoardObjective boardObjective = boardObjectiveService.getBoardObjective(boardObjectiveId, clubId, userId);

        // assert
        verify(boardObjectiveDAO).doesEntityBelongToClub(any(), any());
        verify(boardObjectiveDAO).doesEntityBelongToUser(any(), any());
        verify(boardObjectiveDAO).getEntity(any());
        assertEquals(boardObjectiveId, boardObjective.getId());
        assertEquals(clubId, boardObjective.getClubId());
    }

    /**
     * given an invalid board objective id, tests that the NoResultException thrown from the DAO layer is handled and a
     * service exception is thrown instead
     */
    @Test
    public void getBoardObjectiveWhenBoardObjectiveDataCannotBeFound() {
        // setup
        UUID nonExistentBoardObjectiveId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();
        when(boardObjectiveDAO.doesEntityBelongToClub(eq(nonExistentBoardObjectiveId), eq(clubId)))
                .thenThrow(EntityNotFoundException.class);

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> boardObjectiveService.getBoardObjective(nonExistentBoardObjectiveId, clubId, userId));

        // assert
        verify(boardObjectiveDAO).doesEntityBelongToClub(any(), any());
        verify(boardObjectiveDAO, never()).doesEntityBelongToUser(any(), any());
        verify(boardObjectiveDAO, never()).getEntity(any());
        assertEquals(HttpStatus.NOT_FOUND_404, serviceException.getResponseStatus());
    }

    /**
     * given an invalid board objective id, tests that if the board objective does not belong to the club, no board
     * objective data is returned and a service exception is thrown instead
     */
    @Test
    public void getBoardObjectiveWhenBoardObjectiveDoesNotBelongToClub() {
        // setup
        UUID inaccessibleBoardObjectiveId = UUID.randomUUID();
        UUID accessibleClubId = UUID.randomUUID();
        when(boardObjectiveDAO.doesEntityBelongToClub(eq(inaccessibleBoardObjectiveId), eq(accessibleClubId)))
                .thenReturn(false);

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> boardObjectiveService.getBoardObjective(inaccessibleBoardObjectiveId, accessibleClubId, userId));

        // assert
        verify(boardObjectiveDAO).doesEntityBelongToClub(any(), any());
        verify(boardObjectiveDAO, never()).doesEntityBelongToUser(any(), any());
        verify(boardObjectiveDAO, never()).getEntity(any());
        assertEquals(HttpStatus.CONFLICT_409, serviceException.getResponseStatus());
    }

    /**
     * given an invalid board objective id, tests that if the board objective belongs to a club not accessible by the
     * user, no board objective data is fetched and a service exception is thrown instead.
     */
    @Test
    public void getBoardObjectiveWhenBoardObjectiveDoesNotBelongToUser() {
        // setup
        UUID inaccessibleBoardObjectiveId = UUID.randomUUID();
        UUID inaccessibleClubId = UUID.randomUUID();
        when(boardObjectiveDAO.doesEntityBelongToClub(eq(inaccessibleBoardObjectiveId), eq(inaccessibleClubId)))
                .thenReturn(true);
        when(boardObjectiveDAO.doesEntityBelongToUser(eq(inaccessibleBoardObjectiveId), eq(userId)))
                .thenReturn(false);

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> boardObjectiveService.getBoardObjective(inaccessibleBoardObjectiveId, inaccessibleClubId,
                        userId));

        // assert
        verify(boardObjectiveDAO).doesEntityBelongToClub(any(), any());
        verify(boardObjectiveDAO).doesEntityBelongToUser(any(), any());
        verify(boardObjectiveDAO, never()).getEntity(any());
        assertEquals(HttpStatus.FORBIDDEN_403, serviceException.getResponseStatus());
    }

    /**
     * given an invalid board objective id, tests that if the board objective belongs to a club  that does not exist,
     * no board objective data is fetched and a service exception is thrown instead.
     */
    @Test
    public void getBoardObjectiveWhenAssociatedClubDoesNotExist() {
        // setup
        UUID inaccessibleBoardObjectiveId = UUID.randomUUID();
        UUID inaccessibleClubId = UUID.randomUUID();
        when(boardObjectiveDAO.doesEntityBelongToClub(eq(inaccessibleBoardObjectiveId), eq(inaccessibleClubId)))
                .thenReturn(true);
        when(boardObjectiveDAO.doesEntityBelongToUser(eq(inaccessibleBoardObjectiveId), eq(userId)))
                .thenThrow(NoResultException.class);

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> boardObjectiveService.getBoardObjective(inaccessibleBoardObjectiveId, inaccessibleClubId,
                        userId));

        // assert
        verify(boardObjectiveDAO).doesEntityBelongToClub(any(), any());
        verify(boardObjectiveDAO).doesEntityBelongToUser(any(), any());
        verify(boardObjectiveDAO, never()).getEntity(any());
        assertEquals(HttpStatus.FORBIDDEN_403, serviceException.getResponseStatus());
    }

    /**
     * given a valid board objective entity, tests that the data is persisted correctly
     */
    @Test
    public void createBoardObjectivePersistsBoardObjectiveData() {
        // setup
        UUID clubId = UUID.randomUUID();
        BoardObjective incomingBoardObjective = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder()
                .withClubId(clubId)
                .build();
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
     * corresponding board objective data is updated with the incoming properties and persisted in the DAO layer
     */
    @Test
    public void updateBoardObjectiveUpdatesBoardObjectiveData() {
        // setup
        UUID existingClubId = UUID.randomUUID();
        UUID existingBoardObjectiveId = UUID.randomUUID();
        when(boardObjectiveDAO.doesEntityBelongToClub(eq(existingBoardObjectiveId), eq(existingClubId)))
                .thenReturn(true);
        when(boardObjectiveDAO.doesEntityBelongToUser(eq(existingBoardObjectiveId), eq(userId))).thenReturn(true);
        BoardObjective existingBoardObjective = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder()
                .isExisting(true)
                .withExistingId(existingBoardObjectiveId)
                .withClubId(existingClubId)
                .build();
        when(boardObjectiveDAO.getEntity(eq(existingBoardObjectiveId))).thenReturn(existingBoardObjective);
        ArgumentCaptor<BoardObjective> updatedBoardObjectiveCaptor = ArgumentCaptor.forClass(BoardObjective.class);
        BoardObjective incomingBoardObjective = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder()
                .withClubId(existingClubId)
                .customTitle("updated fake objective title")
                .customDescription("updated fake objective description.")
                .toggleCompletionStatus()
                .build();

        // execute
        BoardObjective updatedBoardObjective = boardObjectiveService.updateBoardObjective(existingBoardObjectiveId,
                existingClubId, userId, incomingBoardObjective);

        // assert
        verify(boardObjectiveDAO).doesEntityBelongToClub(any(), any());
        verify(boardObjectiveDAO).doesEntityBelongToUser(any(), any());
        verify(boardObjectiveDAO).getEntity(any());
        verify(boardObjectiveDAO).updateEntity(eq(existingBoardObjectiveId), updatedBoardObjectiveCaptor.capture());
        BoardObjective capturedBoardObjective = updatedBoardObjectiveCaptor.getValue();
        assertEquals(updatedBoardObjective, capturedBoardObjective);

        assertEquals(existingBoardObjectiveId, capturedBoardObjective.getId());
        assertEquals(existingClubId, capturedBoardObjective.getClubId());
        assertEquals(incomingBoardObjective.getTitle(), capturedBoardObjective.getTitle());
        assertEquals(incomingBoardObjective.getDescription(), capturedBoardObjective.getDescription());
        assertEquals(incomingBoardObjective.getIsCompleted(), capturedBoardObjective.getIsCompleted());

        assertNotNull(capturedBoardObjective.getLastModifiedDate());
        assertTrue(capturedBoardObjective.getLastModifiedDate().isAfter(
                Objects.requireNonNull(existingBoardObjective.getLastModifiedDate())
        ));
    }

    /**
     * given a valid board objective entity and an identifier for an existing board objective, tests that if the board
     * objective for that identifier does not exist, no board objective data is updated and a service exception is
     * thrown instead
     */
    @Test
    public void updateBoardObjectiveWhenBoardObjectiveDoesNotExist() {
        // setup
        UUID nonExistentBoardObjectiveId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();
        when(boardObjectiveDAO.doesEntityBelongToClub(eq(nonExistentBoardObjectiveId), eq(clubId)))
                .thenThrow(EntityNotFoundException.class);

        BoardObjective incomingBoardObjective = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder()
                .withClubId(clubId)
                .customTitle("updated fake objective title")
                .customDescription("updated fake objective description.")
                .toggleCompletionStatus()
                .build();

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> boardObjectiveService.updateBoardObjective(nonExistentBoardObjectiveId, clubId, userId,
                        incomingBoardObjective));

        // assert
        verify(boardObjectiveDAO).doesEntityBelongToClub(any(), any());
        verify(boardObjectiveDAO, never()).doesEntityBelongToUser(any(), any());
        verify(boardObjectiveDAO, never()).getEntity(any());
        verify(boardObjectiveDAO, never()).updateEntity(any(), any());
        assertEquals(HttpStatus.NOT_FOUND_404, serviceException.getResponseStatus());
    }

    /**
     * given an invalid board objective id, tests that if the board objective does not belong to the club, no board
     * objective data is updated and a service exception is thrown instead
     */
    @Test
    public void updateBoardObjectiveWhenBoardObjectiveDoesNotBelongToClub() {
        // setup
        UUID inaccessibleBoardObjectiveId = UUID.randomUUID();
        UUID inaccessibleClubId = UUID.randomUUID();
        UUID accessibleClubId = UUID.randomUUID();
        when(boardObjectiveDAO.doesEntityBelongToClub(eq(inaccessibleBoardObjectiveId), eq(accessibleClubId)))
                .thenReturn(false);

        BoardObjective incomingBoardObjective = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder()
                .withClubId(inaccessibleClubId)
                .customTitle("updated fake objective title")
                .customDescription("updated fake objective description.")
                .toggleCompletionStatus()
                .build();

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> boardObjectiveService.updateBoardObjective(inaccessibleBoardObjectiveId, accessibleClubId, userId,
                        incomingBoardObjective));

        // assert
        verify(boardObjectiveDAO).doesEntityBelongToClub(any(), any());
        verify(boardObjectiveDAO, never()).doesEntityBelongToUser(any(), any());
        verify(boardObjectiveDAO, never()).getEntity(any());
        verify(boardObjectiveDAO, never()).updateEntity(any(), any());
        assertEquals(HttpStatus.CONFLICT_409, serviceException.getResponseStatus());
    }

    /**
     * given an invalid board objective id, tests that if the board objective belongs to a club not accessible by the
     * user, no board objective data is updated and a service exception is thrown instead.
     */
    @Test
    public void updateBoardObjectiveWhenBoardObjectDoesNotBelongToUser() {
        // setup
        UUID inaccessibleBoardObjectiveId = UUID.randomUUID();
        UUID inaccessibleClubId = UUID.randomUUID();
        when(boardObjectiveDAO.doesEntityBelongToClub(eq(inaccessibleBoardObjectiveId), eq(inaccessibleClubId)))
                .thenReturn(true);
        when(boardObjectiveDAO.doesEntityBelongToUser(eq(inaccessibleBoardObjectiveId), eq(userId))).thenReturn(false);
        BoardObjective incomingBoardObjective = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder()
                .withClubId(inaccessibleClubId)
                .customTitle("updated fake objective title")
                .customDescription("updated fake objective description.")
                .toggleCompletionStatus()
                .build();

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> boardObjectiveService.updateBoardObjective(inaccessibleBoardObjectiveId, inaccessibleClubId,
                        userId, incomingBoardObjective));

        // assert
        verify(boardObjectiveDAO).doesEntityBelongToClub(any(), any());
        verify(boardObjectiveDAO).doesEntityBelongToUser(any(), any());
        verify(boardObjectiveDAO, never()).updateEntity(any(), any());
        assertEquals(HttpStatus.FORBIDDEN_403, serviceException.getResponseStatus());
    }

    /**
     * given an invalid board objective id, tests that if the board objective belongs to a club  that does not exist,
     * no board objective data is updated  and a service exception is thrown instead.
     */
    @Test
    public void updateBoardObjectiveWhenAssociatedClubDoesNotExist() {
        // setup
        UUID inaccessibleBoardObjectiveId = UUID.randomUUID();
        UUID inaccessibleClubId = UUID.randomUUID();
        when(boardObjectiveDAO.doesEntityBelongToClub(eq(inaccessibleBoardObjectiveId), eq(inaccessibleClubId)))
                .thenReturn(true);
        when(boardObjectiveDAO.doesEntityBelongToUser(eq(inaccessibleBoardObjectiveId), eq(userId)))
                .thenThrow(NoResultException.class);
        BoardObjective incomingBoardObjective = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder()
                .withClubId(inaccessibleClubId)
                .customTitle("updated fake objective title")
                .customDescription("updated fake objective description.")
                .toggleCompletionStatus()
                .build();

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> boardObjectiveService.updateBoardObjective(inaccessibleBoardObjectiveId, inaccessibleClubId,
                        userId, incomingBoardObjective));

        // assert
        verify(boardObjectiveDAO).doesEntityBelongToClub(any(), any());
        verify(boardObjectiveDAO).doesEntityBelongToUser(any(), any());
        verify(boardObjectiveDAO, never()).updateEntity(any(), any());
        assertEquals(HttpStatus.FORBIDDEN_403, serviceException.getResponseStatus());
    }

    /**
     * given a valid board objective id, removes the board objective entity from the DAO layer
     */
    @Test
    public void deleteBoardObjectiveRemovesBoardObjectiveData() {
        // setup
        UUID boardObjectiveId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();
        when(boardObjectiveDAO.doesEntityBelongToClub(eq(boardObjectiveId), eq(clubId))).thenReturn(true);
        when(boardObjectiveDAO.doesEntityBelongToUser(eq(boardObjectiveId), eq(userId))).thenReturn(true);

        // execute
        boardObjectiveService.deleteBoardObjective(boardObjectiveId, clubId, userId);

        // assert
        verify(boardObjectiveDAO).doesEntityBelongToClub(any(), any());
        verify(boardObjectiveDAO).doesEntityBelongToUser(any(), any());
        verify(boardObjectiveDAO).deleteEntity(eq(boardObjectiveId));
    }

    /**
     * given an invalid board objective id, tests that the NoResultException thrown by the DAO layer is handled
     * and a ServiceException is thrown instead
     */
    @Test
    public void deleteBoardObjectiveWhenBoardObjectiveDataCannotBeNotFound() {
        // setup
        UUID nonExistentBoardObjectiveId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();
        when(boardObjectiveDAO.doesEntityBelongToClub(eq(nonExistentBoardObjectiveId), eq(clubId)))
                .thenThrow(EntityNotFoundException.class);

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> boardObjectiveService.deleteBoardObjective(nonExistentBoardObjectiveId, clubId, userId));

        // assert
        verify(boardObjectiveDAO).doesEntityBelongToClub(any(), any());
        verify(boardObjectiveDAO, never()).doesEntityBelongToUser(any(), any());
        verify(boardObjectiveDAO, never()).deleteEntity(any());
        assertEquals(HttpStatus.NOT_FOUND_404, serviceException.getResponseStatus());
    }

    /**
     * given an invalid board objective id, tests that if the board objective does not belong to the club, no board
     * objective data is deleted and a service exception is thrown instead
     */
    @Test
    public void deleteBoardObjectiveWhenBoardObjectiveDoesNotBelongToClub() {
        // setup
        UUID inaccessibleBoardObjectiveId = UUID.randomUUID();
        UUID accessibleClubId = UUID.randomUUID();
        when(boardObjectiveDAO.doesEntityBelongToUser(eq(inaccessibleBoardObjectiveId), eq(accessibleClubId)))
                .thenReturn(false);

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> boardObjectiveService.deleteBoardObjective(inaccessibleBoardObjectiveId, accessibleClubId,
                        userId));

        // assert
        verify(boardObjectiveDAO).doesEntityBelongToClub(any(), any());
        verify(boardObjectiveDAO, never()).doesEntityBelongToUser(any(), any());
        verify(boardObjectiveDAO, never()).deleteEntity(any());
        assertEquals(HttpStatus.CONFLICT_409, serviceException.getResponseStatus());
    }

    /**
     * given an invalid board objective id, tests that if the board objective belongs to a club not accessible by the
     * user, no board objective data is deleted and a service exception is thrown instead.
     */
    @Test
    public void deleteBoardObjectiveWhenBoardObjectiveDoesNotBelongToUser() {
        // setup
        UUID inaccessibleBoardObjectiveId = UUID.randomUUID();
        UUID inaccessibleClubId = UUID.randomUUID();
        when(boardObjectiveDAO.doesEntityBelongToClub(eq(inaccessibleBoardObjectiveId), eq(inaccessibleClubId)))
                .thenReturn(true);
        when(boardObjectiveDAO.doesEntityBelongToUser(eq(inaccessibleBoardObjectiveId), eq(userId))).thenReturn(false);

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> boardObjectiveService.deleteBoardObjective(inaccessibleBoardObjectiveId, inaccessibleClubId,
                        userId));

        // assert
        verify(boardObjectiveDAO).doesEntityBelongToClub(any(), any());
        verify(boardObjectiveDAO).doesEntityBelongToUser(any(), any());
        verify(boardObjectiveDAO, never()).deleteEntity(any());
        assertEquals(HttpStatus.FORBIDDEN_403, serviceException.getResponseStatus());
    }

    /**
     * given an invalid board objective id, tests that if the board objective belongs to a club  that does not exist,
     * no board objective data is deleted and a service exception is thrown instead.
     */
    @Test
    public void deleteBoardObjectiveWhenAssociatedClubDoesNotExist() {
        // setup
        UUID inaccessibleBoardObjectiveId = UUID.randomUUID();
        UUID inaccessibleClubId = UUID.randomUUID();
        when(boardObjectiveDAO.doesEntityBelongToClub(eq(inaccessibleBoardObjectiveId), eq(inaccessibleClubId)))
                .thenReturn(true);
        when(boardObjectiveDAO.doesEntityBelongToUser(eq(inaccessibleBoardObjectiveId), eq(userId)))
                .thenThrow(NoResultException.class);

        // execute
        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> boardObjectiveService.deleteBoardObjective(inaccessibleBoardObjectiveId, inaccessibleClubId,
                        userId));

        // assert
        verify(boardObjectiveDAO).doesEntityBelongToClub(any(), any());
        verify(boardObjectiveDAO).doesEntityBelongToUser(any(), any());
        verify(boardObjectiveDAO, never()).deleteEntity(any());
        assertEquals(HttpStatus.FORBIDDEN_403, serviceException.getResponseStatus());
    }

    /**
     * given a valid club id, tests that all the board objective data associated with it are fetched from persistent
     * storage and returned
     */
    @Test
    public void getAllBoardObjectivesForClubFetchesAllBoardObjectivesAssociatedWithClub() {
        // setup
        UUID existingClubId = UUID.randomUUID();
        List<BoardObjective> boardObjectivesAssociatedWithClub =
                BoardObjectiveDataProvider.getMultipleBoardObjectives(existingClubId);
        when(boardObjectiveDAO.getBoardObjectivesForClub(eq(existingClubId)))
                .thenReturn(boardObjectivesAssociatedWithClub);

        // execute
        List<BoardObjective> boardObjectives = boardObjectiveService.getAllBoardObjectivesForClub(existingClubId);

        // assert
        verify(boardObjectiveDAO).getBoardObjectivesForClub(any());
        assertFalse(boardObjectives.isEmpty());
        assertEquals(boardObjectivesAssociatedWithClub, boardObjectives);
    }
}