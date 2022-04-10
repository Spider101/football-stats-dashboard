package com.footballstatsdashboard.db.jdbi;

import com.footballstatsdashboard.api.model.AuthToken;
import com.footballstatsdashboard.db.IAuthTokenEntityDAO;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindPojo;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import javax.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthTokenJdbiDAO implements IAuthTokenEntityDAO {
    private final IAuthTokenDAO authTokenDAO;

    public AuthTokenJdbiDAO(Jdbi jdbi) {
        this.authTokenDAO = jdbi.onDemand(IAuthTokenDAO.class);
        this.authTokenDAO.createTable();
    }

    public void insertEntity(AuthToken entity) {
        this.authTokenDAO.insert(entity);
    }

    public AuthToken getEntity(UUID entityId) throws EntityNotFoundException {
        AuthToken authToken = this.authTokenDAO.findById(entityId.toString());
        if (authToken == null) {
            throw new EntityNotFoundException();
        }
        return authToken;
    }

    public void updateEntity(UUID existingEntityId, AuthToken updatedEntity) {
        this.authTokenDAO.update(existingEntityId.toString(), updatedEntity.getLastAccessUTC());
    }

    public void deleteEntity(UUID entityId) throws EntityNotFoundException {
        this.authTokenDAO.delete(entityId.toString());
    }

    public Optional<AuthToken> getAuthTokenForUser(UUID userId) {
        List<AuthToken> authTokens = this.authTokenDAO.findByUserId(userId.toString());
        if (authTokens.size() == 1) {
            return Optional.of(authTokens.get(0));
        }
        return Optional.empty();
    }

    private interface IAuthTokenDAO {
        @SqlUpdate(
                "CREATE TABLE IF NOT EXISTS authToken (id VARCHAR PRIMARY KEY, userId VARCHAR," +
                        " lastAccessUTC TIMESTAMP, FOREIGN KEY (userId) REFERENCES user (id))"
        )
        void createTable();

        @SqlUpdate("INSERT INTO authToken (id, userId, lastAccessUTC) values (:id, :userId, :lastAccessUTC)")
        void insert(@BindPojo AuthToken authToken);

        @SqlQuery("SELECT * FROM authToken WHERE id = :id")
        AuthToken findById(@Bind("id") String authTokenId);

        @SqlUpdate("UPDATE authToken SET lastAccessUTC = :lastAccessUTC WHERE id = :id")
        void update(@Bind("id") String existingAuthTokenId, @Bind("lastAccessUTC") Instant updatedLastAccessUTC);

        @SqlUpdate("DELETE FROM authToken where id = :id")
        void delete(@Bind("id") String authTokenId);

        @SqlQuery("SELECT * FROM authToken WHERE userId = :userId")
        List<AuthToken> findByUserId(@Bind("userId") String userId);
    }
}