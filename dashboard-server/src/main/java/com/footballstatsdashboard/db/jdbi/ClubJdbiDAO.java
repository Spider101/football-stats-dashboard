package com.footballstatsdashboard.db.jdbi;

import com.footballstatsdashboard.api.model.Club;
import com.footballstatsdashboard.api.model.ImmutableClub;
import com.footballstatsdashboard.api.model.club.ClubSummary;
import com.footballstatsdashboard.api.model.club.Expenditure;
import com.footballstatsdashboard.api.model.club.ImmutableExpenditure;
import com.footballstatsdashboard.api.model.club.ImmutableIncome;
import com.footballstatsdashboard.api.model.club.ImmutableManagerFunds;
import com.footballstatsdashboard.api.model.club.ImmutableSquadPlayer;
import com.footballstatsdashboard.api.model.club.Income;
import com.footballstatsdashboard.api.model.club.ManagerFunds;
import com.footballstatsdashboard.api.model.club.SquadPlayer;
import com.footballstatsdashboard.db.IClubEntityDAO;
import com.footballstatsdashboard.db.jdbi.rowmappers.SquadPlayerRowMapper;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindPojo;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClubJdbiDAO implements IClubEntityDAO {
    private final IClubDAO clubDAO;
    private final IManagerFundsHistoryDAO managerFundsHistoryDAO;
    private final IIncomeHistoryDAO incomeHistoryDAO;
    private final IExpenditureHistoryDAO expenditureHistoryDAO;
    public ClubJdbiDAO(Jdbi jdbi) {
        this.clubDAO = jdbi.onDemand(IClubDAO.class);
        this.clubDAO.createTable();

        this.managerFundsHistoryDAO = jdbi.onDemand(IManagerFundsHistoryDAO.class);
        this.managerFundsHistoryDAO.createTable();

        this.incomeHistoryDAO = jdbi.onDemand(IIncomeHistoryDAO.class);
        this.incomeHistoryDAO.createTable();

        this.expenditureHistoryDAO = jdbi.onDemand(IExpenditureHistoryDAO.class);
        this.expenditureHistoryDAO.createTable();
    }

    @Override
    public void insertEntity(Club entity) {
        this.clubDAO.insert(entity);

        Instant createdAt = Instant.now();
        this.managerFundsHistoryDAO.insert(UUID.randomUUID().toString(), entity.getManagerFunds().getCurrent(),
                entity.getId().toString(), createdAt);
        this.incomeHistoryDAO.insert(UUID.randomUUID().toString(), entity.getIncome().getCurrent(),
                entity.getId().toString(), createdAt);
        this.expenditureHistoryDAO.insert(UUID.randomUUID().toString(), entity.getExpenditure().getCurrent(),
                entity.getId().toString(), createdAt);
    }

    @Override
    public Club getEntity(UUID entityId) throws EntityNotFoundException {
        return this.clubDAO.findById(entityId.toString())
                .map(this::buildClubEntity)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public void updateEntity(UUID existingEntityId, Club updatedEntity) {
        this.clubDAO.update(existingEntityId.toString(), updatedEntity);

        // income and expenditure are updated separately; not part of the club entity update logic.
        Instant createdAt = Instant.now();
        this.managerFundsHistoryDAO.insert(UUID.randomUUID().toString(), updatedEntity.getManagerFunds().getCurrent(),
                existingEntityId.toString(), createdAt);
    }

    @Override
    public void deleteEntity(UUID entityId) throws EntityNotFoundException {
        this.managerFundsHistoryDAO.delete(entityId.toString());
        this.incomeHistoryDAO.delete(entityId.toString());
        this.expenditureHistoryDAO.delete(entityId.toString());
        this.clubDAO.delete(entityId.toString());
    }

    @Override
    public boolean doesEntityBelongToUser(UUID entityId, UUID userId) {
        return this.clubDAO.findUserIdAssociatedWithClub(entityId.toString())
                .map(userIdAssociatedWithClub -> userIdAssociatedWithClub.equals(userId.toString()))
                .orElseThrow(NoResultException::new);
    }

    public List<ClubSummary> getClubSummariesForUser(UUID userId) {
        return this.clubDAO.findClubsByUserId(userId.toString());
    }

    public List<SquadPlayer> getPlayersInClub(UUID clubId) {
        List<SquadPlayer> baseSquadPlayerEntities = this.clubDAO.getPlayersForClub(clubId.toString());
        if (baseSquadPlayerEntities == null) {
            throw new EntityNotFoundException();
        }
        return baseSquadPlayerEntities.stream()
                .map(squadPlayer -> {
                    List<Float> recentForm = this.clubDAO.getRecentFormForPlayer(squadPlayer.getPlayerId().toString());
                    return ImmutableSquadPlayer.builder()
                            .from(squadPlayer)
                            .recentForm(recentForm)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private ImmutableClub buildClubEntity(Club baseClubEntity) {
        List<BigDecimal> managerFundsHistory =
                this.managerFundsHistoryDAO.getManagerFundsHistoryForClub(baseClubEntity.getId().toString());
        List<BigDecimal> incomeHistory =
                this.incomeHistoryDAO.getIncomeHistoryForClub(baseClubEntity.getId().toString());
        List<BigDecimal> expenditureHistory =
                this.expenditureHistoryDAO.getExpenditureHistoryForClub(baseClubEntity.getId().toString());
        ManagerFunds managerFunds = ImmutableManagerFunds.builder()
                .from(baseClubEntity.getManagerFunds())
                .history(managerFundsHistory)
                .build();
        Income income = ImmutableIncome.builder()
                .current(incomeHistory.get(0))
                .history(incomeHistory)
                .build();
        Expenditure expenditure = ImmutableExpenditure.builder()
                .current(expenditureHistory.get(0))
                .history(expenditureHistory)
                .build();
        return ImmutableClub.builder()
                .from(baseClubEntity)
                .managerFunds(managerFunds)
                .income(income)
                .expenditure(expenditure)
                .build();
    }

    private interface IClubDAO {

        @SqlUpdate(
                "CREATE TABLE IF NOT EXISTS club (id VARCHAR PRIMARY KEY, name VARCHAR, logo VARCHAR," +
                        " transferBudget BIGINT, wageBudget BIGINT, userId VARCHAR," +
                        " createdDate DATE, lastModifiedDate DATE, createdBy VARCHAR, type VARCHAR," +
                        " FOREIGN KEY (userId) REFERENCES user (id))"
        )
        void createTable();

        @SqlUpdate(
                "INSERT INTO club (id, name, logo, transferBudget, wageBudget, userId, createdDate," +
                        " lastModifiedDate, createdBy, type)" +
                        "VALUES (:id, :name, :logo, :transferBudget, :wageBudget, :userId," +
                        " :createdDate, :lastModifiedDate, :createdBy, :type)"
        )
        void insert(@BindPojo Club newClub);

        @SqlQuery("SELECT TOP 1 c.id, name, logo, transferBudget, wageBudget, managerFunds AS managerFunds_current," +
                " userId, createdDate, lastModifiedDate, createdBy" +
                " FROM club c LEFT JOIN managerFundsHistory mfh ON c.id = mfh.clubId WHERE c.id = :id")
        Optional<Club> findById(@Bind("id") String clubId);

        @SqlQuery("SELECT id AS clubId, name, logo, createdDate FROM club WHERE userId = :userId")
        List<ClubSummary> findClubsByUserId(@Bind("userId") String userId);

        @SqlQuery("SELECT userId FROM club WHERE id = :clubId")
        Optional<String> findUserIdAssociatedWithClub(@Bind("clubId") String clubId);

        @SqlQuery(
                "SELECT p.id, pm.name, pm.country, pm.countryLogo as countryFlag," +
                        " (SELECT TOP 1 name FROM playerRole pr WHERE pr.playerId = p.id) as role," +
                        " (SELECT ability FROM playerAbilityHistory pah WHERE pah.playerId = p.id" +
                        " ORDER BY pah.createdAt DESC LIMIT 1) as currentAbility," +
                        " FROM player p LEFT JOIN playerMetadata pm ON p.id = pm.playerId" +
                        " WHERE p.clubId = :clubId"
        )
        @RegisterRowMapper(SquadPlayerRowMapper.class)
        List<SquadPlayer> getPlayersForClub(@Bind("clubId") String clubId);

        @SqlQuery(
                "SELECT (SELECT matchRating FROM matchRatingHistory mrh WHERE mrh.matchPerformanceId = mp.id" +
                        " ORDER BY createdAt DESC LIMIT 1 ) as currentMatchRating FROM matchPerformance mp" +
                        " WHERE mp.playerId = :playerId ORDER BY mp.createdDate DESC LIMIT 5"
        )
        List<Float> getRecentFormForPlayer(@Bind("playerId") String playerId);

        @SqlUpdate(
                "UPDATE club SET name = :name, logo = :logo, transferBudget = :transferBudget," +
                        " wageBudget = :wageBudget, lastModifiedDate = :lastModifiedDate WHERE id = :id"
        )
        void update(@Bind("id") String existingClubId, @BindPojo Club updatedClub);

        @SqlUpdate("DELETE FROM club WHERE id = :id")
        void delete(@Bind("id") String clubId);
    }

    private interface IManagerFundsHistoryDAO {
        @SqlUpdate(
                "CREATE TABLE IF NOT EXISTS managerFundsHistory (id VARCHAR PRIMARY KEY, managerFunds BIGINT," +
                        " createdAt TIMESTAMP, clubID VARCHAR, FOREIGN KEY (clubId) REFERENCES club (id))"
        )
        void createTable();

        @SqlUpdate(
                "INSERT INTO managerFundsHistory (id, managerFunds, clubId, createdAt)" +
                        " VALUES (:id, :managerFunds, :clubId, :createdAt)"
        )
        void insert(@Bind("id") String managerFundsId,
                    @Bind("managerFunds") BigDecimal managerFunds,
                    @Bind("clubId") String clubId,
                    @Bind("createdAt") Instant createdAt);

        @SqlQuery("SELECT managerFunds FROM managerFundsHistory WHERE clubId = :clubId ORDER BY createdAt DESC")
        List<BigDecimal> getManagerFundsHistoryForClub(@Bind("clubId") String clubId);

        @SqlUpdate("DELETE FROM managerFundsHistory WHERE clubId = :clubId")
        void delete(@Bind("clubId") String clubId);
    }

    private interface IIncomeHistoryDAO {
        @SqlUpdate(
                "CREATE TABLE IF NOT EXISTS incomeHistory (id VARCHAR PRIMARY KEY, income BIGINT," +
                        " createdAt TIMESTAMP, clubID VARCHAR, FOREIGN KEY (clubId) REFERENCES club (id))"
        )
        void createTable();

        @SqlUpdate(
                "INSERT INTO incomeHistory (id, income, clubId, createdAt) VALUES (:id, :income, :clubId, :createdAt)"
        )
        void insert(@Bind("id") String incomeId,
                    @Bind("income") BigDecimal income,
                    @Bind("clubId") String clubId,
                    @Bind("createdAt") Instant createdAt);

        @SqlQuery("SELECT income FROM incomeHistory WHERE clubId = :clubId ORDER BY createdAt DESC")
        List<BigDecimal> getIncomeHistoryForClub(@Bind("clubId") String clubId);

        @SqlUpdate("DELETE FROM incomeHistory WHERE clubId = :clubId")
        void delete(@Bind("clubId") String clubId);
    }

    private interface IExpenditureHistoryDAO {
        @SqlUpdate(
                "CREATE TABLE IF NOT EXISTS expenditureHistory (id VARCHAR PRIMARY KEY, expenditure BIGINT," +
                        " createdAt TIMESTAMP, clubID VARCHAR, FOREIGN KEY (clubId) REFERENCES club (id))"
        )
        void createTable();

        @SqlUpdate(
                "INSERT into expenditureHistory (id, expenditure, clubId, createdAt)" +
                        " VALUES (:id, :expenditure, :clubId, :createdAt)"
        )
        void insert(@Bind("id") String expenditureId,
                    @Bind("expenditure") BigDecimal expenditure,
                    @Bind("clubId") String clubId,
                    @Bind("createdAt") Instant createdAt);

        @SqlQuery("SELECT expenditure FROM expenditureHistory WHERE clubId = :clubId ORDER BY createdAt DESC")
        List<BigDecimal> getExpenditureHistoryForClub(@Bind("clubId") String clubId);

        @SqlUpdate("DELETE FROM expenditureHistory WHERE clubId = :clubId")
        void delete(@Bind("clubId") String clubId);
    }
}