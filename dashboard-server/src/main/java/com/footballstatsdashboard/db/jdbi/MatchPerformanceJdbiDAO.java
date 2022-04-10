package com.footballstatsdashboard.db.jdbi;

import com.footballstatsdashboard.api.model.ImmutableMatchPerformance;
import com.footballstatsdashboard.api.model.MatchPerformance;
import com.footballstatsdashboard.api.model.matchPerformance.ImmutableMatchRating;
import com.footballstatsdashboard.api.model.matchPerformance.MatchRating;
import com.footballstatsdashboard.db.IMatchPerformanceEntityDAO;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindPojo;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import javax.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MatchPerformanceJdbiDAO implements IMatchPerformanceEntityDAO {

    private final IMatchPerformanceDAO matchPerformanceDAO;
    private final IMatchRatingHistoryDAO matchRatingHistoryDAO;
    public MatchPerformanceJdbiDAO(Jdbi jdbi) {
        this.matchPerformanceDAO = jdbi.onDemand(IMatchPerformanceDAO.class);
        this.matchRatingHistoryDAO = jdbi.onDemand(IMatchRatingHistoryDAO.class);

        this.matchPerformanceDAO.createTable();
        this.matchRatingHistoryDAO.createTable();
    }

    @Override
    public void insertEntity(MatchPerformance entity) {
        this.matchPerformanceDAO.insert(entity);

        MatchRating matchRating = entity.getMatchRating();
        this.matchRatingHistoryDAO.insert(UUID.randomUUID().toString(), matchRating.getCurrent(),
                entity.getId().toString(), entity.getCompetitionId().toString(), Instant.now());
    }

    @Override
    public MatchPerformance getEntity(UUID entityId) throws EntityNotFoundException {
        MatchPerformance baseMatchPerformanceEntity = this.matchPerformanceDAO.findById(entityId.toString());
        if (baseMatchPerformanceEntity == null) {
            throw new EntityNotFoundException();
        }
        List<Float> matchRatingHistory =
                this.matchRatingHistoryDAO.getMatchRatingHistoryForMatchPerformance(entityId.toString());
        MatchRating matchRating = ImmutableMatchRating.builder()
                .from(baseMatchPerformanceEntity.getMatchRating())
                .history(matchRatingHistory)
                .build();
        return ImmutableMatchPerformance.builder()
                .from(baseMatchPerformanceEntity)
                .matchRating(matchRating)
                .build();
    }

    @Override
    public void updateEntity(UUID existingEntityId, MatchPerformance updatedEntity) {
        this.matchPerformanceDAO.update(existingEntityId.toString(), updatedEntity);

        // TODO: 22/03/22 check if match rating has been updated and only then insert into history table
        this.matchRatingHistoryDAO.insert(UUID.randomUUID().toString(), updatedEntity.getMatchRating().getCurrent(),
                existingEntityId.toString(), updatedEntity.getCompetitionId().toString(), Instant.now());
    }

    @Override
    public void deleteEntity(UUID entityId) throws EntityNotFoundException {
        this.matchRatingHistoryDAO.delete(entityId.toString());
        this.matchPerformanceDAO.delete(entityId.toString());
    }

    @Override
    public List<MatchPerformance> getMatchPerformanceOfPlayerInCompetition(UUID playerId, UUID competitionId) {
        List<MatchPerformance> matchPerformances =
                this.matchPerformanceDAO.findByPlayerIdAndCompetitionId(playerId.toString(), competitionId.toString());
        return matchPerformances.stream()
                .map(matchPerformance -> {
                    List<Float> matchRatingHistory = this.matchRatingHistoryDAO
                            .getMatchRatingHistoryForMatchPerformance(matchPerformance.getId().toString());
                    MatchRating matchRating = ImmutableMatchRating.builder()
                            .from(matchPerformance.getMatchRating())
                            .history(matchRatingHistory)
                            .build();
                    return ImmutableMatchPerformance.builder()
                            .from(matchPerformance)
                            .matchRating(matchRating)
                            .build();
                }).collect(Collectors.toList());
    }

    private interface IMatchPerformanceDAO {
        @SqlUpdate(
                "CREATE TABLE IF NOT EXISTS matchPerformance" +
                        "(id VARCHAR PRIMARY KEY, playerId VARCHAR, competitionId VARCHAR, appearances INT," +
                        " goals INT, penalties INT, assists INT, playerOfTheMatch INT, yellowCards INT, redCards INT," +
                        " tackles INT, fouls INT, dribbles INT, passCompletionRate FLOAT, createdDate DATE," +
                        " lastModifiedDate DATE, createdBy VARCHAR, type VARCHAR," +
                        " FOREIGN KEY (playerId) REFERENCES player (id))"
        )
        void createTable();

        @SqlUpdate(
                "INSERT INTO matchPerformance (id, playerId, competitionId, appearances, goals, penalties, assists," +
                        " playerOfTheMatch, yellowCards, redCards, tackles, fouls, dribbles, passCompletionRate," +
                        " createdDate, lastModifiedDate, createdBy)" +
                        " VALUES (:id, :playerId, :competitionId, :appearances, :goals, :penalties, :assists," +
                        " :playerOfTheMatch, :yellowCards, :redCards, :tackles, :fouls, :dribbles," +
                        " :passCompletionRate, :createdDate, :lastModifiedDate, :createdBy)"
        )
        void insert(@BindPojo MatchPerformance matchPerformance);

        @SqlQuery(
                "SELECT mp.id, playerId, competitionId, appearances, goals, penalties, assists, playerOfTheMatch," +
                        " (SELECT matchRating FROM matchRatingHistory mrh WHERE mrh.matchPerformanceId = mp.id" +
                        " ORDER BY mrh.createdAt DESC LIMIT 1) as matchRating_current, yellowCards, redCards," +
                        " tackles, fouls, dribbles, passCompletionRate, createdDate, lastModifiedDate, createdBy" +
                        " FROM matchPerformance mp WHERE mp.id = :id"
        )
        MatchPerformance findById(@Bind("id") String matchPerformanceId);

        @SqlQuery(
                "SELECT mp.id, playerId, competitionId, appearances, goals, penalties, assists, playerOfTheMatch," +
                        " (SELECT matchRating FROM matchRatingHistory mrh WHERE mrh.matchPerformanceId = mp.id" +
                        " ORDER BY mrh.createdAt DESC LIMIT 1) as matchRating_current, yellowCards, redCards," +
                        " tackles, fouls, dribbles, passCompletionRate, createdDate, lastModifiedDate, createdBy" +
                        " FROM matchPerformance mp WHERE mp.playerId = :playerId AND mp.competitionId = :competitionId"
        )
        List<MatchPerformance> findByPlayerIdAndCompetitionId(
                @Bind("playerId") String playerId,
                @Bind("competitionId") String competitionId);

        @SqlUpdate("UPDATE matchPerformance SET appearances = :appearances, goals = :goals, penalties = :penalties," +
                " assists = :assists, playerOfTheMatch = :playerOfTheMatch, yellowCards = :yellowCards," +
                " redCards = :redCards, tackles = :tackles, fouls = :fouls, dribbles = :dribbles," +
                " passCompletionRate = :passCompletionRate, lastModifiedDate = :lastModifiedDate WHERE id = :id")
        void update(@Bind("id") String existingMatchPerformanceId, @BindPojo MatchPerformance updatedMatchPerformance);

        @SqlUpdate("DELETE FROM matchPerformance WHERE id = :id")
        void delete(@Bind("id") String matchPerformanceId);
    }

    private interface IMatchRatingHistoryDAO {
        @SqlUpdate(
                "CREATE TABLE IF NOT EXISTS matchRatingHistory (id VARCHAR PRIMARY KEY, matchRating FLOAT," +
                        " matchPerformanceId VARCHAR, competitionId VARCHAR, createdAt TIMESTAMP," +
                        " FOREIGN KEY (matchPerformanceId) REFERENCES matchPerformance (id)," +
                        " FOREIGN KEY (competitionId) REFERENCES matchPerformance (competitionId))")
        void createTable();

        @SqlUpdate(
                "INSERT INTO matchRatingHistory (id, matchRating, matchPerformanceId, competitionId, createdAt)" +
                        " VALUES (:id, :matchRating, :matchPerformanceId, :competitionId, :createdAt)")
        void insert(
                @Bind("id") String matchRatingId,
                @Bind("matchRating") Float matchRating,
                @Bind("matchPerformanceId") String matchPerformanceId,
                @Bind("competitionId") String competitionId,
                @Bind("createdAt") Instant created);

        @SqlQuery(
                "SELECT matchRating FROM matchRatingHistory WHERE matchPerformanceId = :matchPerformanceId" +
                        " ORDER BY createdAt DESC"
        )
        List<Float> getMatchRatingHistoryForMatchPerformance(@Bind("matchPerformanceId") String matchPerformanceId);

        @SqlUpdate("DELETE FROM matchRatingHistory WHERE matchPerformanceId = :matchPerformanceId")
        void delete(@Bind("matchPerformanceId") String matchPerformanceId);
    }
}