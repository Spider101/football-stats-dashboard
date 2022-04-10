package com.footballstatsdashboard.db.jdbi;

import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.db.IUserEntityDAO;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindPojo;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserJdbiDAO implements IUserEntityDAO {
    private final IUserDAO userDAO;

    public UserJdbiDAO(Jdbi jdbi) {
        this.userDAO = jdbi.onDemand(IUserDAO.class);
        this.userDAO.createTable();
    }

    public void insertEntity(User entity) {
        this.userDAO.insert(entity);
    }

    public User getEntity(UUID entityId) throws EntityNotFoundException {
        User user = this.userDAO.findById(entityId.toString());
        if (user == null) {
            throw new EntityNotFoundException();
        }
        return user;
    }

    public void updateEntity(UUID existingEntityId, User updatedEntity) {
        this.userDAO.update(existingEntityId.toString(), updatedEntity);
    }

    public void deleteEntity(UUID entityId) throws EntityNotFoundException {
        User user = this.userDAO.findById(entityId.toString());
        if (user == null) {
            throw new EntityNotFoundException();
        }
        // delete only when the entity exists
        this.userDAO.delete(entityId.toString());
    }

    public List<User> getExistingUsers(String firstName, String lastName, String emailAddress) {
        return this.userDAO.findExistingUsers(firstName, lastName, emailAddress);
    }

    public Optional<User> getUserByEmailAddress(String emailAddress) {
        List<User> users = this.userDAO.findByEmailAddress(emailAddress);
        if (users.size() == 1) {
            return Optional.of(users.get(0));
        }
        return Optional.empty();
    }

    private interface IUserDAO {
        @SqlUpdate(
                "CREATE TABLE IF NOT EXISTS user (id VARCHAR PRIMARY KEY, firstName VARCHAR(100)," +
                        " lastName VARCHAR(100), email VARCHAR(100), password VARCHAR(100), createdBy VARCHAR (100)," +
                        " createdDate DATE, lastModifiedDate DATE, role VARCHAR, type VARCHAR)"
        )
        void createTable();

        @SqlUpdate(
                "INSERT INTO user" +
                    " (id, firstName, lastName, email, password, createdBy, createdDate, lastModifiedDate," +
                        " role, type)" +
                    " VALUES (:id, :firstName, :lastName, :email, :password, :createdBy, :createdDate," +
                        ":lastModifiedDate, :role, :type)"
        )
        void insert(@BindPojo User newUser);

        @SqlQuery("SELECT * FROM user WHERE id = :id")
        User findById(@Bind("id") String userId);

        @SqlQuery("SELECT * FROM user where firstName = :firstName AND lastName = :lastName AND email = :email")
        List<User> findExistingUsers(
                @Bind("firstName") String firstName,
                @Bind("lastName") String lastName,
                @Bind("email") String emailAddress);

        @SqlQuery("SELECT * FROM user WHERE email = :email")
        List<User> findByEmailAddress(@Bind("email") String emailAddress);

        @SqlUpdate(
                "UPDATE user" +
                " SET firstName = :firstName, lastName = :lastName, email = :email, password = :password," +
                " lastModifiedDate = :lastModifiedDate WHERE id = :id"
        )
        void update(@Bind("id") String existingUserId, @BindPojo User updatedUser);

        @SqlUpdate("DELETE FROM user WHERE id = :id")
        void delete(@Bind("id") String userId);
    }
}