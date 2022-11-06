package com.footballstatsdashboard.services;

import com.footballstatsdashboard.api.model.club.BoardObjective;
import com.footballstatsdashboard.api.model.club.ImmutableBoardObjective;
import com.footballstatsdashboard.core.exceptions.ServiceException;
import com.footballstatsdashboard.db.IBoardObjectiveEntityDAO;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class BoardObjectiveService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BoardObjectiveService.class);

    private final IBoardObjectiveEntityDAO boardObjectiveDAO;

    public BoardObjectiveService(IBoardObjectiveEntityDAO boardObjectiveDAO) {
        this.boardObjectiveDAO = boardObjectiveDAO;
    }

    public BoardObjective getBoardObjective(UUID boardObjectiveId, UUID clubId, UUID authorizedUserId) {
        try {
            // verify that the board objective being fetched belongs to the club in the request
            if (!this.boardObjectiveDAO.doesEntityBelongToClub(boardObjectiveId, clubId)) {
                String errorMessage = String.format("Club ID in the request: %s does not match club ID stored in" +
                        " the existing board objective entity (ID: %s)", clubId, boardObjectiveId);
                LOGGER.error(errorMessage);
                throw new ServiceException(HttpStatus.CONFLICT_409, errorMessage);
            }
        } catch (EntityNotFoundException entityNotFoundException) {
            String errorMessage = String.format("No board objective entity found for ID: %s", boardObjectiveId);
            LOGGER.error(errorMessage);
            throw new ServiceException(HttpStatus.NOT_FOUND_404, errorMessage);
        }

        try {
            // verify that the current user has access to the board objective they are trying to fetch
            if (!this.boardObjectiveDAO.doesEntityBelongToUser(boardObjectiveId, authorizedUserId)) {
                LOGGER.error("Cannot fetch board objective with ID: {} that does not belong to user making request",
                        boardObjectiveId);
                throw new ServiceException(HttpStatus.FORBIDDEN_403,
                        "User does not have access to this board objective!");
            }
        } catch (NoResultException noResultException) {
            LOGGER.error("Cannot fetch board objective with ID: {} because the associated club does not exist!",
                    boardObjectiveId);
            throw new ServiceException(HttpStatus.FORBIDDEN_403, "User does not have access to this board objective!");
        }

        return this.boardObjectiveDAO.getEntity(boardObjectiveId);
    }

    public BoardObjective createBoardObjective(BoardObjective incomingBoardObjective, UUID clubId, String createdBy) {
        BoardObjective newBoardObjective = ImmutableBoardObjective.builder()
                .from(incomingBoardObjective)
                .clubId(clubId)
                .createdDate(LocalDate.now())
                .lastModifiedDate(LocalDate.now())
                .createdBy(createdBy)
                .build();
        this.boardObjectiveDAO.insertEntity(newBoardObjective);
        return newBoardObjective;
    }

    public BoardObjective updateBoardObjective(UUID existingBoardObjectiveId, UUID clubId,
                                                UUID authorizedUserId, BoardObjective incomingBoardObjective) {
        BoardObjective existingBoardObjective =
                this.getBoardObjective(existingBoardObjectiveId, clubId, authorizedUserId);

        BoardObjective updatedBoardObjective = ImmutableBoardObjective.builder()
                .from(existingBoardObjective)
                .title(incomingBoardObjective.getTitle())
                .description(incomingBoardObjective.getDescription())
                .isCompleted(incomingBoardObjective.getIsCompleted())
                .lastModifiedDate(LocalDate.now())
                .build();

        this.boardObjectiveDAO.updateEntity(existingBoardObjectiveId, updatedBoardObjective);
        return updatedBoardObjective;
    }

    public void deleteBoardObjective(UUID boardObjectiveId, UUID clubId, UUID authorizedUserId) {
        try {
            // verify that the board objective being deleted belongs to the club in the request
            if (!this.boardObjectiveDAO.doesEntityBelongToClub(boardObjectiveId, clubId)) {
                String errorMessage = String.format("Club ID in the request: %s does not match club ID stored in" +
                        " the existing board objective entity (ID: %s)", clubId, boardObjectiveId);
                LOGGER.error(errorMessage);
                throw new ServiceException(HttpStatus.CONFLICT_409, errorMessage);
            }
        } catch (EntityNotFoundException entityNotFoundException) {
            String errorMessage = String.format("No board objective entity found for ID: %s", boardObjectiveId);
            LOGGER.error(errorMessage);
            throw new ServiceException(HttpStatus.NOT_FOUND_404, errorMessage);
        }

        try {
            // verify that the current user has access to the board objective they are trying to delete
            if (!this.boardObjectiveDAO.doesEntityBelongToUser(boardObjectiveId, authorizedUserId)) {
                LOGGER.error("Cannot delete board objective with ID: {} that does not belong to user making request",
                        boardObjectiveId);
                throw new ServiceException(HttpStatus.FORBIDDEN_403,
                        "User does not have access to this board objective!");
            }
        } catch (NoResultException noResultException) {
            LOGGER.error("Cannot delete board objective with ID: {} because the associated club does not exist!",
                    boardObjectiveId);
            throw new ServiceException(HttpStatus.FORBIDDEN_403, "User does not have access to this board objective!");
        }

        this.boardObjectiveDAO.deleteEntity(boardObjectiveId);
    }

    public List<BoardObjective> getAllBoardObjectivesForClub(UUID clubId) {
        return this.boardObjectiveDAO.getBoardObjectivesForClub(clubId);
    }
}