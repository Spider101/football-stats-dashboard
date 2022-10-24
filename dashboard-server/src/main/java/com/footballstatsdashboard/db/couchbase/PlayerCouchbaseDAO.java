package com.footballstatsdashboard.db.couchbase;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.ExistsResult;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.footballstatsdashboard.api.model.Player;
import com.footballstatsdashboard.db.IPlayerEntityDAO;
import com.footballstatsdashboard.db.key.CouchbaseKeyProvider;
import com.footballstatsdashboard.db.key.ResourceKey;
import io.dropwizard.setup.Environment;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class PlayerCouchbaseDAO extends CouchbaseDAO implements IPlayerEntityDAO {
    private final CouchbaseKeyProvider<ResourceKey> keyProvider;

    public PlayerCouchbaseDAO(CouchbaseKeyProvider<ResourceKey> keyProvider,
                                Supplier<Cluster> clusterSupplier,
                                Supplier<Bucket> bucketSupplier,
                                Environment environment) {
        super(clusterSupplier, bucketSupplier, environment);
        this.keyProvider = keyProvider;
    }

    public void insertEntity(Player entity) {
        ResourceKey key = new ResourceKey(entity.getId());
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.getCouchbaseBucket().defaultCollection().insert(documentKey, entity);
    }

    public Player getEntity(UUID entityId) {
        ResourceKey key = new ResourceKey(entityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        try {
            GetResult result = this.getCouchbaseBucket().defaultCollection().get(documentKey);
            return result.contentAs(Player.class);
        } catch (DocumentNotFoundException documentNotFoundException) {
            throw new EntityNotFoundException(documentNotFoundException.getMessage());
        }
    }

    public void updateEntity(UUID existingEntityId, Player updatedEntity) {
        ResourceKey key = new ResourceKey(existingEntityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.getCouchbaseBucket().defaultCollection().replace(documentKey, updatedEntity);
    }

    public void deleteEntity(UUID entityId) {
        ResourceKey key = new ResourceKey(entityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        try {
            this.getCouchbaseBucket().defaultCollection().remove(documentKey);
        } catch (DocumentNotFoundException documentNotFoundException) {
            throw new EntityNotFoundException(documentNotFoundException.getMessage());
        }
    }

    @Override
    public boolean doesEntityBelongToUser(UUID entityId, UUID userId) {
        String bucketName = this.getCouchbaseBucket().name();
        String query = String.format("SELECT club.userId FROM `%s` AS player JOIN `%s` AS club" +
                        " ON player.clubId = club.id" +
                        " WHERE club.type = 'Club' AND player.id = $playerId", bucketName, bucketName);

        QueryOptions queryOptions = QueryOptions.queryOptions().parameters(
                JsonObject.create().put("playerId", entityId.toString())
        );
        QueryResult queryResult = this.getCouchbaseCluster().query(query, queryOptions);
        List<JsonObject> resultRows = queryResult.rowsAsObject();

        if (resultRows.size() == 1) {
            UUID userIdAssociatedWithPlayer = UUID.fromString(resultRows.get(0).getString("userId"));
            return userIdAssociatedWithPlayer.equals(userId);
        }

        // TODO: 30/04/21 figure out a way to handle query result having more than one userId
        //  (bad state; should not happen)
        return false;
    }

    @Override
    public boolean doesEntityExist(UUID entityId) {
        ResourceKey key = new ResourceKey(entityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        ExistsResult result = this.getCouchbaseBucket().defaultCollection().exists(documentKey);
        return result.exists();
    }
}