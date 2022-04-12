package com.footballstatsdashboard.db.jdbi;

import com.footballstatsdashboard.api.model.club.BoardObjective;
import com.footballstatsdashboard.db.IBoardObjectiveEntityDAO;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindPojo;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

public class BoardObjectiveJdbiDAO implements IBoardObjectiveEntityDAO {
    private final IBoardObjectivesDAO boardObjectivesDAO;

    public BoardObjectiveJdbiDAO(Jdbi jdbi) {
        this.boardObjectivesDAO = jdbi.onDemand(IBoardObjectivesDAO.class);
        this.boardObjectivesDAO.createTable();
    }

    @Override
    public void insertEntity(BoardObjective entity) {
        this.boardObjectivesDAO.insert(entity);
    }

    @Override
    public BoardObjective getEntity(UUID entityId) throws EntityNotFoundException {
        BoardObjective boardObjective = this.boardObjectivesDAO.findById(entityId.toString());
        if (boardObjective == null) {
            throw new EntityNotFoundException();
        }
        return boardObjective;
    }

    @Override
    public void updateEntity(UUID existingEntityId, BoardObjective updatedEntity) {
        this.boardObjectivesDAO.update(existingEntityId.toString(), updatedEntity);
    }

    @Override
    public void deleteEntity(UUID entityId) throws EntityNotFoundException {
        this.boardObjectivesDAO.delete(entityId.toString());
    }

    @Override
    public List<BoardObjective> getBoardObjectivesForClub(UUID clubId) {
        List<BoardObjective> boardObjectives = this.boardObjectivesDAO.findByClubId(clubId.toString());
        if (boardObjectives == null) {
            throw new EntityNotFoundException();
        }
        return boardObjectives;
    }

    private interface IBoardObjectivesDAO {
        @SqlUpdate(
                "CREATE TABLE IF NOT EXISTS boardObjectives (id VARCHAR PRIMARY KEY, clubId VARCHAR, title VARCHAR," +
                        " description VARCHAR, isCompleted BYTE, FOREIGN KEY (clubId) REFERENCES club (id))"
        )
        void createTable();

        @SqlUpdate(
                "INSERT INTO boardObjectives (id, clubId, title, description, isCompleted)" +
                        " VALUES (:id, :clubId, :title, :description, :isCompleted)"
        )
        void insert(@BindPojo BoardObjective boardObjective);

        @SqlQuery("SELECT * FROM boardObjectives WHERE id = :id")
        BoardObjective findById(@Bind("id") String boardObjectiveId);

        @SqlQuery("SELECT * FROM boardObjectives WHERE clubId = :clubId")
        List<BoardObjective> findByClubId(@Bind("clubId") String clubId);

        @SqlUpdate(
                "UPDATE boardObjectives SET title = :title, description = :description, isCompleted = :isCompleted" +
                        " WHERE id = :id"
        )
        void update(@Bind("id") String existingBoardObjectiveId, @BindPojo BoardObjective updatedBoardObjective);

        @SqlUpdate("DELETE FROM boardObjectives WHERE id = :id")
        void delete(@Bind("id") String boardObjectiveId);
    }
}