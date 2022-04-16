package com.footballstatsdashboard.services;

import com.footballstatsdashboard.api.model.club.BoardObjective;
import com.footballstatsdashboard.api.model.club.ImmutableBoardObjective;
import com.footballstatsdashboard.core.exceptions.ServiceException;
import com.footballstatsdashboard.db.IBoardObjectiveEntityDAO;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class BoardObjectiveService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BoardObjectiveService.class);

    private final IBoardObjectiveEntityDAO boardObjectiveDAO;

    public BoardObjectiveService(IBoardObjectiveEntityDAO boardObjectiveDAO) {
        this.boardObjectiveDAO = boardObjectiveDAO;
    }

    public BoardObjective getBoardObjective(UUID boardObjectiveId, UUID clubId) {
        BoardObjective existingBoardObjective;

        try {
            existingBoardObjective = this.boardObjectiveDAO.getEntity(boardObjectiveId);
        } catch (EntityNotFoundException entityNotFoundException) {
            String errorMessage = String.format("No board objective entity found for ID: %s", boardObjectiveId);
            LOGGER.error(errorMessage);
            throw new ServiceException(HttpStatus.NOT_FOUND_404, errorMessage);
        }

        // assert that the board objective belongs to the club
        if (!existingBoardObjective.getClubId().equals(clubId)) {
            String errorMessage = String.format("Board objective with ID: %s does not belong to club with ID: %s",
                    existingBoardObjective.getId(), clubId);
            LOGGER.error(errorMessage);
            throw new ServiceException(HttpStatus.FORBIDDEN_403, errorMessage);
        }

        return existingBoardObjective;
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
                                                BoardObjective incomingBoardObjective) {
        BoardObjective existingBoardObjective = this.getBoardObjective(existingBoardObjectiveId, clubId);
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

    public void deleteBoardObjective(UUID boardObjectiveId, UUID clubId) {
        BoardObjective existingBoardObjective = this.getBoardObjective(boardObjectiveId, clubId);

        try {
            this.boardObjectiveDAO.deleteEntity(boardObjectiveId);
        } catch (EntityNotFoundException entityNotFoundException) {
            String errorMessage = String.format("No board objective entity found for ID: %s", boardObjectiveId);
            LOGGER.error(errorMessage);
            throw new ServiceException(HttpStatus.NOT_FOUND_404, errorMessage);
        }
    }

    public List<BoardObjective> getAllBoardObjectivesForClub(UUID clubId) {
        return this.boardObjectiveDAO.getBoardObjectivesForClub(clubId);
    }
}