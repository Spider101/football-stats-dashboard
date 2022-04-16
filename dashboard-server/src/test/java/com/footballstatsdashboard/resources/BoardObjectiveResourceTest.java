package com.footballstatsdashboard.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.footballstatsdashboard.BoardObjectiveDataProvider;
import com.footballstatsdashboard.api.model.ImmutableUser;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.api.model.club.BoardObjective;
import com.footballstatsdashboard.api.model.club.ImmutableBoardObjective;
import com.footballstatsdashboard.core.exceptions.ServiceException;
import com.footballstatsdashboard.services.BoardObjectiveService;
import com.footballstatsdashboard.services.ClubService;
import io.dropwizard.jackson.Jackson;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BoardObjectiveResourceTest {

    private static final ObjectMapper OBJECT_MAPPER = Jackson.newObjectMapper().copy();
    private static final String URI_PATH = "/board-objective";

    private User userPrincipal;
    private BoardObjectiveResource boardObjectiveResource;

    @Mock
    private UriInfo uriInfo;

    @Mock
    private BoardObjectiveService boardObjectiveService;

    @Mock
    private ClubService clubService;

    /**
     * set up test data before each test case is run
     */
    @Before
    public void initialize() {
        MockitoAnnotations.openMocks(this);

        UriBuilder uriBuilder = UriBuilder.fromPath(URI_PATH);
        when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);

        userPrincipal = ImmutableUser.builder()
                .email("fake email")
                // other details are not required for the purposes of this test so using empty strings
                .password("")
                .firstName("")
                .lastName("")
                .build();

        boardObjectiveResource = new BoardObjectiveResource(boardObjectiveService, clubService);
    }

    /**
     * given a valid board objective id, tests that the board objective entity is successfully fetched
     * and returned in the response
     */
    @Test
    public void getBoardObjectiveFetchesBoardObjectiveData() {
        // setup
        UUID clubId = UUID.randomUUID();
        UUID boardObjectiveId = UUID.randomUUID();
        BoardObjective existingBoardObjective = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder()
                .isExisting(true)
                .withExistingId(boardObjectiveId)
                .withClubId(clubId)
                .build();
        when(boardObjectiveService.getBoardObjective(eq(boardObjectiveId), eq(clubId))).thenReturn(existingBoardObjective);

        // execute
        Response boardObjectiveResponse = boardObjectiveResource.getBoardObjective(userPrincipal, clubId,
                boardObjectiveId);

        // assert
        verify(clubService).getClub(any(), any());
        verify(boardObjectiveService).getBoardObjective(any(), any());

        assertEquals(HttpStatus.OK_200, boardObjectiveResponse.getStatus());
        assertNotNull(boardObjectiveResponse.getEntity());

        BoardObjective boardObjectiveFromResponse =
                OBJECT_MAPPER.convertValue(boardObjectiveResponse.getEntity(), BoardObjective.class);
        assertEquals(boardObjectiveId, boardObjectiveFromResponse.getId());
        assertEquals(clubId, boardObjectiveFromResponse.getClubId());
    }

    /**
     *  given an ID for a board objective associated with a club the user does not have access to, tests that the board
     *  objective data is not returned and a service exception is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void getBoardObjectiveWhenClubDoesNotBelongToUser() {
        // setup
        UUID invalidClubId = UUID.randomUUID();
        when(clubService.getClub(eq(invalidClubId), eq(userPrincipal.getId())))
                .thenThrow(new ServiceException(HttpStatus.FORBIDDEN_403, "User does not have access to club!"));

        // execute
        boardObjectiveResource.getBoardObjective(userPrincipal, invalidClubId, UUID.randomUUID()    );

        // assert
        verify(clubService).getClub(any(), any());
        verify(boardObjectiveService, never()).getBoardObjective(any(), any());
    }

    /**
     * given and ID for a board objective associated with a club that does not exist, tests that the objective data is
     * not returned and a service exception is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void getBoardObjectiveWhenClubDoesNotExist() {
        // setup
        UUID nonExistentClubId = UUID.randomUUID();
        when(clubService.getClub(eq(nonExistentClubId), eq(userPrincipal.getId())))
                .thenThrow(new ServiceException(HttpStatus.NOT_FOUND_404, "No club found!"));

        // execute
        boardObjectiveResource.getBoardObjective(userPrincipal, nonExistentClubId, UUID.randomUUID());

        // assert
        verify(clubService).getClub(any(), any());
        verify(boardObjectiveService, never()).getBoardObjective(any(), any());
    }

    /**
     * given a valid board objective entity in the request, tests that the board objective is successfully persisted
     */
    @Test
    public void createBoardObjectivePersistsBoardObjectiveData() {
        // setup
        UUID clubId = UUID.randomUUID();
        BoardObjective incomingBoardObjective = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder().build();
        BoardObjective createdBoardObjective = ImmutableBoardObjective.builder()
                .from(incomingBoardObjective)
                .clubId(clubId)
                .build();
        when(boardObjectiveService.createBoardObjective(eq(incomingBoardObjective), eq(clubId),
                eq(userPrincipal.getEmail()))).thenReturn(createdBoardObjective);

        // execute
        Response boardObjectiveResponse = boardObjectiveResource.createBoardObjective(userPrincipal, clubId,
                incomingBoardObjective, uriInfo);

        // assert
        verify(boardObjectiveService).createBoardObjective(any(), any(), anyString());
        assertEquals(HttpStatus.CREATED_201, boardObjectiveResponse.getStatus());
        assertNotNull(boardObjectiveResponse.getEntity());
        // a boardObjectiveId is set on the boardObjective instance created despite not setting one explicitly due to
        // the way the interface has been set up
        assertEquals(URI_PATH + "/" + incomingBoardObjective.getId().toString(),
                boardObjectiveResponse.getLocation().getPath());

        // ensure the new board objective contains the club id for the club it is associated with
        BoardObjective boardObjectiveFromResponse =
                OBJECT_MAPPER.convertValue(boardObjectiveResponse.getEntity(), BoardObjective.class);
        assertEquals(clubId, boardObjectiveFromResponse.getClubId());
    }

    /**
     * given a valid board objective entity in the request with a club id for a club that doesn't exist, tests that no
     * data is created and a service exception is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void createBoardObjectiveWhenClubForBoardObjectiveDoesNotExist() {
        // setup
        UUID nonExistentClubId = UUID.randomUUID();
        when(clubService.getClub(eq(nonExistentClubId), eq(userPrincipal.getId())))
                .thenThrow(new ServiceException(HttpStatus.NOT_FOUND_404, "No club found!"));
        BoardObjective incomingBoardObjective = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder().build();

        // execute
        boardObjectiveResource.createBoardObjective(userPrincipal, nonExistentClubId, incomingBoardObjective, uriInfo);

        // assert
        verify(clubService).getClub(any(), any());
        verify(boardObjectiveService, never()).createBoardObjective(any(), any(), anyString());
    }

    /**
     * given a valid board objective entity in the request with a club id for a club that the current user does not have
     * access to, tests that no board objective data is created and a service exception is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void createBoardObjectiveWhenClubForBoardObjectiveIsNotAccessible() {
        // setup
        UUID inaccessibleClubId = UUID.randomUUID();
        when(clubService.getClub(eq(inaccessibleClubId), eq(userPrincipal.getId())))
                .thenThrow(new ServiceException(HttpStatus.FORBIDDEN_403, "User does not have access to club!"));
        BoardObjective incomingBoardObjective = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder().build();

        // execute
        boardObjectiveResource.createBoardObjective(userPrincipal, inaccessibleClubId, incomingBoardObjective, uriInfo);

        // assert
        verify(clubService).getClub(any(), any());
        verify(boardObjectiveService, never()).createBoardObjective(any(), any(), anyString());
    }

    /**
     * given a valid board objective entity in the request, tests that the corresponding board objective data is updated
     */
    @Test
    public void updateBoardObjectiveUpdatesBoardObjectiveData() {
        // setup
        UUID existingClubId = UUID.randomUUID();
        UUID existingBoardObjectiveId = UUID.randomUUID();
        BoardObjective incomingBoardObjective = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder().build();
        BoardObjective updatedBoardObjectiveBase = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder()
                .withExistingId(existingBoardObjectiveId)
                .withClubId(existingClubId)
                .build();
        BoardObjective updatedBoardObjective = BoardObjectiveDataProvider.ModifiedBoardObjectiveBuilder.builder()
                .from(updatedBoardObjectiveBase)
                .isForUpdatedEntity(true)
                .build();
        when(boardObjectiveService.updateBoardObjective(eq(existingBoardObjectiveId), eq(existingClubId),
                eq(incomingBoardObjective))).thenReturn(updatedBoardObjective);

        // execute
        Response boardObjectiveResponse = boardObjectiveResource.updateBoardObjective(userPrincipal, existingClubId,
                existingBoardObjectiveId, incomingBoardObjective);

        // assert
        verify(clubService).getClub(any(), any());
        verify(boardObjectiveService).updateBoardObjective(any(), any(), any());

        assertNotNull(boardObjectiveResponse);
        assertEquals(HttpStatus.OK_200, boardObjectiveResponse.getStatus());
        assertNotNull(boardObjectiveResponse.getEntity());

        BoardObjective boardObjectiveFromResponse =
                OBJECT_MAPPER.convertValue(boardObjectiveResponse.getEntity(), BoardObjective.class);
        assertEquals(existingBoardObjectiveId, boardObjectiveFromResponse.getId());

        assertEquals(existingBoardObjectiveId, boardObjectiveFromResponse.getId());
        assertEquals(existingClubId, boardObjectiveFromResponse.getClubId());
        assertEquals(incomingBoardObjective.getTitle(), boardObjectiveFromResponse.getTitle());
        assertEquals(incomingBoardObjective.getDescription(), boardObjectiveFromResponse.getDescription());
        assertEquals(incomingBoardObjective.getIsCompleted(), boardObjectiveFromResponse.getIsCompleted());
    }

    /**
     * given a valid board objective entity in the request, tests that the if the user does not have access to the club
     * with which the board objective is associated, the board objective data is not updated and a service exceptions is
     * thrown
     */
    @Test(expected = ServiceException.class)
    public void updateBoardObjectiveWhenAssociatedClubDoesNotBelongToUser() {
        // setup
        UUID existingBoardObjectiveId = UUID.randomUUID();
        UUID inaccessibleClubId = UUID.randomUUID();
        when(clubService.getClub(eq(inaccessibleClubId), eq(userPrincipal.getId())))
                .thenThrow(new ServiceException(HttpStatus.FORBIDDEN_403, "User does not have access to club!"));
        BoardObjective incomingBoardObjective = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder().build();

        // execute
        boardObjectiveResource.updateBoardObjective(userPrincipal, inaccessibleClubId, existingBoardObjectiveId,
                incomingBoardObjective);

        // assert
        verify(clubService).getClub(any(), any());
        verify(boardObjectiveService, never()).getBoardObjective(any(), any());
        verify(boardObjectiveService, never()).updateBoardObjective(any(), any(), any());
    }

    /**
     * given a valid board objective entity in the request, tests that if the club associated with the board objective
     * data does not exist, no board objective data is updated and a service exception is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void updateBoardObjectiveWhenClubDoesNotExist() {
        // setup
        UUID boardObjectiveId = UUID.randomUUID();
        UUID nonExistentClubId = UUID.randomUUID();
        when(clubService.getClub(eq(nonExistentClubId), eq(userPrincipal.getId())))
                .thenThrow(new ServiceException(HttpStatus.NOT_FOUND_404, "No club found!"));
        BoardObjective incomingBoardObjective = BoardObjectiveDataProvider.BoardObjectiveBuilder.builder().build();

        // execute
        boardObjectiveResource.updateBoardObjective(userPrincipal, nonExistentClubId, boardObjectiveId,
                incomingBoardObjective);

        // assert
        verify(clubService).getClub(any(), any());
        verify(boardObjectiveService, never()).getBoardObjective(any(), any());
        verify(boardObjectiveService, never()).updateBoardObjective(any(), any(), any());
    }

    /**
     * given a valid board objective id, tests that the board objective data is removed and a 204 No Content response is
     * returned
     */
    @Test
    public void deleteBoardObjectiveRemovesBoardObjectiveData() {
        // setup
        UUID clubId = UUID.randomUUID();
        UUID boardObjectiveId = UUID.randomUUID();
        when(clubService.doesClubBelongToUser(eq(clubId), eq(userPrincipal.getId()))).thenReturn(true);

        // execute
        Response boardObjectiveResponse = boardObjectiveResource.deleteBoardObjective(userPrincipal, clubId,
                boardObjectiveId);

        // assert
        verify(boardObjectiveService).deleteBoardObjective(eq(boardObjectiveId), any());
        assertNotNull(boardObjectiveResponse);
        assertEquals(HttpStatus.NO_CONTENT_204, boardObjectiveResponse.getStatus());
    }

    /**
     * given a valid board objective id, tests that if the user does not have access to the club associated with the
     * board objective, no board objective data is removed and a service exception is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void deleteBoardObjectiveWhenBoardObjectiveDoesNotBelongToUser() {
        // setup
        UUID inaccessibleClubId = UUID.randomUUID();
        when(clubService.doesClubBelongToUser(eq(inaccessibleClubId), eq(userPrincipal.getId()))).thenReturn(false);

        // execute
        boardObjectiveResource.deleteBoardObjective(userPrincipal, inaccessibleClubId, UUID.randomUUID());

        // assert
        verify(clubService).doesClubBelongToUser(any(), any());
        verify(boardObjectiveService, never()).deleteBoardObjective(any(), any());
    }

    /**
     * given a valid club id, tests that all board objectives associated with the club are returned
     */
    @Test
    public void getAllBoardObjectivesForClubFetchesAllBoardObjectivesDataAssociatedWithClub() {
        // setup
        UUID clubId = UUID.randomUUID();
        when(boardObjectiveService.getAllBoardObjectivesForClub(eq(clubId)))
                .thenReturn(BoardObjectiveDataProvider.getMultipleBoardObjectives(clubId));
        when(clubService.doesClubBelongToUser(eq(clubId), eq(userPrincipal.getId()))).thenReturn(true);

        // execute
        Response boardObjectiveResponse = boardObjectiveResource.getAllBoardObjectives(userPrincipal, clubId);

        // assert
        verify(clubService).doesClubBelongToUser(any(), any());
        verify(boardObjectiveService).getAllBoardObjectivesForClub(any());

        assertNotNull(boardObjectiveResponse);
        assertEquals(HttpStatus.OK_200, boardObjectiveResponse.getStatus());
        assertNotNull(boardObjectiveResponse.getEntity());

        TypeReference<List<BoardObjective>> boardObjectiveListRef = new TypeReference<>() {};
        List<BoardObjective> boardObjectiveList =
                OBJECT_MAPPER.convertValue(boardObjectiveResponse.getEntity(), boardObjectiveListRef);
        assertFalse(boardObjectiveList.isEmpty());
        boardObjectiveList.forEach(boardObjective -> assertEquals(clubId, boardObjective.getClubId()));
    }

    /**
     * given a valid club id, tests that if the user does not have access to the club the board objectives are being
     * requested for, no board objective data is returned and a service exception is thrown instead
     */
    @Test(expected = ServiceException.class)
    public void getAllBoardObjectivesForClubWhenClubDoesNotBelongToUser() {
        // setup
        UUID inaccessibleClubId = UUID.randomUUID();
        when(clubService.doesClubBelongToUser(eq(inaccessibleClubId), eq(userPrincipal.getId()))).thenReturn(false);

        // execute
        boardObjectiveResource.getAllBoardObjectives(userPrincipal, inaccessibleClubId);

        // assert
        verify(clubService).doesClubBelongToUser(any(), any());
        verify(boardObjectiveService, never()).getAllBoardObjectivesForClub(any());
    }
}