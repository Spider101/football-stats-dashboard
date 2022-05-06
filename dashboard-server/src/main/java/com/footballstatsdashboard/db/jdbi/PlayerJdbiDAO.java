package com.footballstatsdashboard.db.jdbi;

import com.footballstatsdashboard.api.model.ImmutablePlayer;
import com.footballstatsdashboard.api.model.Player;
import com.footballstatsdashboard.api.model.player.Ability;
import com.footballstatsdashboard.api.model.player.Attribute;
import com.footballstatsdashboard.api.model.player.ImmutableAbility;
import com.footballstatsdashboard.api.model.player.ImmutableAttribute;
import com.footballstatsdashboard.api.model.player.Metadata;
import com.footballstatsdashboard.api.model.player.Role;
import com.footballstatsdashboard.db.IPlayerEntityDAO;
import com.footballstatsdashboard.db.jdbi.rowmappers.PlayerRoleRowMapper;
import com.footballstatsdashboard.db.jdbi.rowmappers.PlayerRowMapper;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindPojo;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerJdbiDAO implements IPlayerEntityDAO {
    private final IPlayerDAO playerDAO;
    private final IPlayerMetadataDAO playerMetadataDAO;
    private final IPlayerAbilityHistoryDAO playerAbilityHistoryDAO;
    private final IPlayerRoleDAO playerRoleDAO;
    private final IPlayerAttributeDAO playerAttributeDAO;
    private final IPlayerAttributeHistoryDAO playerAttributeHistoryDAO;

    public PlayerJdbiDAO(Jdbi jdbi) {
        this.playerDAO = jdbi.onDemand(IPlayerDAO.class);
        this.playerDAO.createTable();

        this.playerMetadataDAO = jdbi.onDemand(IPlayerMetadataDAO.class);
        this.playerMetadataDAO.createTable();

        this.playerAbilityHistoryDAO = jdbi.onDemand(IPlayerAbilityHistoryDAO.class);
        this.playerAbilityHistoryDAO.createTable();

        this.playerRoleDAO = jdbi.onDemand(IPlayerRoleDAO.class);
        this.playerRoleDAO.createTable();

        this.playerAttributeDAO = jdbi.onDemand(IPlayerAttributeDAO.class);
        this.playerAttributeDAO.createTable();

        this.playerAttributeHistoryDAO = jdbi.onDemand(IPlayerAttributeHistoryDAO.class);
        this.playerAttributeHistoryDAO.createTable();
    }

    @Override
    public void insertEntity(Player entity) {
        this.playerDAO.insert(entity);
        this.playerMetadataDAO.insert(entity.getMetadata(), entity.getId().toString());

        Instant createdAt = Instant.now();
        this.playerAbilityHistoryDAO.insert(UUID.randomUUID().toString(), entity.getId().toString(),
                entity.getAbility().getCurrent(), createdAt);

        entity.getRoles().forEach(role -> {
            String associatedAttributes = String.join(",", role.getAssociatedAttributes());
            this.playerRoleDAO.insert(UUID.randomUUID().toString(), entity.getId().toString(), role.getName(),
                    associatedAttributes);
        });

        entity.getAttributes().forEach(attribute -> {
            String attributeId = UUID.randomUUID().toString();
            this.playerAttributeDAO.insert(attributeId, entity.getId().toString(), attribute.getName(),
                    attribute.getCategory().toString(), attribute.getGroup().toString());
            // first entry in the history table for the given attribute
            this.playerAttributeHistoryDAO.insert(UUID.randomUUID().toString(), attribute.getName(),
                    attribute.getValue(), createdAt);
        });
    }

    @Override
    public Player getEntity(UUID entityId) throws EntityNotFoundException {
        return this.playerDAO.findById(entityId.toString())
                .map(this::buildPlayerEntity)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public void updateEntity(UUID existingEntityId, Player updatedEntity) {
        this.playerDAO.update(existingEntityId.toString(), updatedEntity);
        this.playerMetadataDAO.update(existingEntityId.toString(), updatedEntity.getMetadata());

        Instant createdAt = Instant.now();
        this.playerAbilityHistoryDAO.insert(UUID.randomUUID().toString(), existingEntityId.toString(),
                updatedEntity.getAbility().getCurrent(), createdAt);

        // roles is a list of roles associated with the player; best option is to delete existing roles and insert the
        // roles again for the updated player entity
        this.playerRoleDAO.delete(existingEntityId.toString());
        updatedEntity.getRoles().forEach(role -> {
            String associatedAttributes = String.join(",", role.getAssociatedAttributes());
            this.playerRoleDAO.insert(UUID.randomUUID().toString(), existingEntityId.toString(), role.getName(),
                    associatedAttributes);
        });

        updatedEntity.getAttributes().forEach(updatedAttribute -> {
            // to keep things simple, assume only attribute value changes so just add a row in the history table
            this.playerAttributeHistoryDAO.insert(UUID.randomUUID().toString(), updatedAttribute.getName(),
                    updatedAttribute.getValue(), createdAt);
        });
    }

    @Override
    public void deleteEntity(UUID entityId) throws EntityNotFoundException {
        this.playerMetadataDAO.delete(entityId.toString());
        this.playerAbilityHistoryDAO.delete(entityId.toString());
        this.playerRoleDAO.delete(entityId.toString());
        this.playerAttributeHistoryDAO.delete(entityId.toString());
        this.playerAttributeDAO.delete(entityId.toString());
        this.playerDAO.delete(entityId.toString());
    }

    @Override
    public boolean doesEntityBelongToUser(UUID entityId, UUID userId) {
        return this.playerDAO.findUserIdAssociatedWithPlayer(entityId.toString())
                .map(userIdAssociatedWithPlayer -> userIdAssociatedWithPlayer.equals(userId.toString()))
                .orElseThrow(NoResultException::new);
    }

    @Override
    public boolean doesEntityExist(UUID entityId) {
        return this.playerDAO.findById(entityId.toString()).isPresent();
    }

    private ImmutablePlayer buildPlayerEntity(Player basePlayerEntity) {
        List<Integer> abilityHistory =
                this.playerAbilityHistoryDAO.getAbilityHistoryForPlayer(basePlayerEntity.getId().toString());
        Ability playerAbility = ImmutableAbility.builder()
                // the history is sorted so the latest value is first
                .current(abilityHistory.get(0))
                .history(abilityHistory)
                .build();

        List<Role> playerRoles = this.playerRoleDAO.getRolesForPlayer(basePlayerEntity.getId().toString());

        List<Attribute> attributes =
                this.playerAttributeDAO.getAttributesForPlayer(basePlayerEntity.getId().toString()).stream()
                        .map(attribute -> {
                            List<Integer> attributeHistory = this.playerAttributeHistoryDAO
                                    .getHistoryForAttributeByName(attribute.getName());
                            return ImmutableAttribute.builder()
                                    .from(attribute)
                                    .history(attributeHistory)
                                    .build();
                        }).collect(Collectors.toList());

        return ImmutablePlayer.builder()
                .from(basePlayerEntity)
                .ability(playerAbility)
                .roles(playerRoles)
                .attributes(attributes)
                .build();
    }

    private interface IPlayerDAO {
        @SqlUpdate(
                "CREATE TABLE IF NOT EXISTS player (id VARCHAR PRIMARY KEY, clubId VARCHAR, createdDate DATE," +
                        " lastModifiedDate DATE, createdBy VARCHAR, FOREIGN KEY (clubId) REFERENCES club (id))")
        void createTable();

        @SqlUpdate(
                "INSERT INTO player (id, clubId, createdDate, lastModifiedDate, createdBy)" +
                        " VALUES (:id, :clubId, :createdDate, :lastModifiedDate, :createdBy)"
        )
        void insert(@BindPojo Player player);

        @SqlQuery(
                "SELECT p.id, name, club, country, photo, clubLogo, countryLogo, age, clubId, createdBy, createdDate," +
                        " lastModifiedDate FROM player p LEFT JOIN playerMetadata pm ON p.id = pm.playerId" +
                        " WHERE p.id = :id"
        )
        @RegisterRowMapper(PlayerRowMapper.class)
        Optional<Player> findById(@Bind("id") String playerId);

        @SqlQuery("SELECT c.userId FROM player p LEFT JOIN club c ON p.clubId = c.id WHERE p.id = :playerId")
        Optional<String> findUserIdAssociatedWithPlayer(@Bind("playerId") String playerId);

        @SqlUpdate("UPDATE player SET lastModifiedDate = :lastModifiedDate, createdBy = :createdBy WHERE id = :id")
        void update(@Bind("id") String existingPlayerId, @BindPojo Player updatedPlayer);

        @SqlUpdate("DELETE FROM player WHERE id = :id")
        void delete(@Bind("id") String playerId);
    }

    private interface IPlayerMetadataDAO {
        @SqlUpdate(
                "CREATE TABLE IF NOT EXISTS playerMetadata (id VARCHAR PRIMARY KEY AUTO_INCREMENT, playerId VARCHAR," +
                        " name VARCHAR, club VARCHAR, country VARCHAR, photo VARCHAR, clubLogo VARCHAR," +
                        " countryLogo VARCHAR, age INT, FOREIGN KEY (playerId) REFERENCES player (id))"
        )
        void createTable();

        @SqlUpdate(
                "INSERT INTO playerMetadata (playerId, name, club, country, photo, clubLogo, countryLogo, age)" +
                        " VALUES (:playerId, :name, :club, :country, :photo, :clubLogo, :countryLogo, :age)"
        )
        void insert(@BindPojo Metadata playerMetadata, @Bind("playerId") String playerId);

        @SqlUpdate(
                "UPDATE playerMetadata SET name = :name, club = :club, country = :country, photo = :photo," +
                        " clubLogo = :clubLogo, countryLogo = :countryLogo, age = :age WHERE playerId = :playerId")
        void update(@Bind("playerId") String existingPlayerId, @BindPojo Metadata updatedMetadata);

        @SqlUpdate("DELETE FROM playerMetadata WHERE playerId = :playerId")
        void delete(@Bind("playerId") String playerId);
    }

    private interface IPlayerAbilityHistoryDAO {
        @SqlUpdate(
                "CREATE TABLE IF NOT EXISTS playerAbilityHistory (id VARCHAR PRIMARY KEY, playerId VARCHAR," +
                        " ability INT, createdAt TIMESTAMP, FOREIGN KEY (playerId) REFERENCES player (id))")
        void createTable();

        @SqlUpdate(
                "INSERT INTO playerAbilityHistory (id, playerId, ability, createdAt)" +
                        " VALUES (:id, :playerId, :ability, :createdAt)"
        )
        void insert(
                @Bind("id") String playerAbilityId,
                @Bind("playerId") String playerId,
                @Bind("ability") int playerAbility,
                @Bind("createdAt") Instant createdAt);

        @SqlQuery("SELECT ability FROM playerAbilityHistory WHERE playerId = :playerId ORDER BY createdAt DESC")
        List<Integer> getAbilityHistoryForPlayer(@Bind("playerId") String playerId);

        @SqlUpdate("DELETE FROM playerAbilityHistory WHERE playerId = :playerId")
        void delete(@Bind("playerId") String playerId);
    }

    private interface IPlayerRoleDAO {
        @SqlUpdate(
                "CREATE TABLE IF NOT EXISTS playerRole (id VARCHAR PRIMARY KEY, playerId VARCHAR, name VARCHAR, " +
                        " associatedAttributes VARCHAR, FOREIGN KEY (playerId) REFERENCES player (id))"
        )
        void createTable();

        @SqlUpdate(
                "INSERT INTO playerRole (id, playerId, name, associatedAttributes)" +
                        " VALUES (:id, :playerId, :name, :associatedAttributes)"
        )
        void insert(
                @Bind("id") String roleId,
                @Bind("playerId") String playerId,
                @Bind("name") String roleName,
                @Bind("associatedAttributes") String associatedAttributes);

        @SqlQuery("SELECT name, associatedAttributes FROM playerRole WHERE playerId = :playerId")
        @RegisterRowMapper(PlayerRoleRowMapper.class)
        List<Role> getRolesForPlayer(@Bind("playerId") String playerId);

        @SqlUpdate("DELETE FROM playerRole WHERE playerId = :playerId")
        void delete(@Bind("playerId") String playerId);
    }

    private interface IPlayerAttributeDAO {
        @SqlUpdate(
                "CREATE TABLE IF NOT EXISTS playerAttribute (id VARCHAR PRIMARY KEY, playerId VARCHAR, name VARCHAR," +
                        " category VARCHAR, \"group\" VARCHAR, FOREIGN KEY (playerId) REFERENCES player (id))"
        )
        void createTable();

        @SqlUpdate(
                "INSERT INTO playerAttribute (id, playerId, name, category, \"group\")" +
                        " VALUES (:id, :playerId, :name, :category, :group)")
        void insert(
                @Bind("id") String playerAttributeId,
                @Bind("playerId") String playerId,
                @Bind("name") String attributeName,
                @Bind("category") String attributeCategory,
                @Bind("group") String attributeGroup);

        @SqlQuery(
                "SELECT name, category, \"group\", (SELECT attributeValue FROM playerAttributeHistory pah" +
                        " WHERE pah.attributeName = pa.name ORDER BY pah.createdAt DESC LIMIT 1) as value" +
                        " FROM playerAttribute pa WHERE pa.playerId = :playerId"
        )
        List<Attribute> getAttributesForPlayer(@Bind("playerId") String playerId);

        @SqlUpdate(
                "UPDATE playerAttribute SET name = :name, category = :category, \"group\" = :group" +
                        " WHERE playerId = :playerId")
        void update(
                @Bind("name") String attributeName,
                @Bind("category") String attributeCategory,
                @Bind("group") String attributeGroup,
                @Bind("playerId") String playerId);

        @SqlUpdate("DELETE FROM playerAttribute WHERE playerId = :playerId")
        void delete(@Bind("playerId") String playerId);

    }

    private interface IPlayerAttributeHistoryDAO {
        @SqlUpdate(
                "CREATE TABLE IF NOT EXISTS playerAttributeHistory (id VARCHAR PRIMARY KEY, attributeName VARCHAR," +
                        " attributeValue INT, createdAt TIMESTAMP," +
                        " FOREIGN KEY (attributeName) REFERENCES playerAttribute (name))"
        )
        void createTable();

        @SqlUpdate(
                "INSERT INTO playerAttributeHistory (id, attributeName, attributeValue, createdAt)" +
                        " VALUES (:id, :attributeName, :attributeValue, :createdAt)"
        )
        void insert(
                @Bind("id") String id,
                @Bind("attributeName") String attributeName,
                @Bind("attributeValue") int attributeValue,
                @Bind("createdAt") Instant createdAt);

        @SqlQuery("SELECT attributeValue FROM playerAttributeHistory WHERE attributeName = :attributeName")
        List<Integer> getHistoryForAttributeByName(@Bind("attributeName") String attributeName);

        @SqlUpdate(
                "DELETE FROM playerAttributeHistory WHERE attributeName IN" +
                        " (SELECT name FROM playerAttribute WHERE playerId = :playerId)"
        )
        void delete(@Bind("playerId") String playerId);
    }
}