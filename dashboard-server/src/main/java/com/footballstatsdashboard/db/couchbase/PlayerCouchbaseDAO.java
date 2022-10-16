package com.footballstatsdashboard.db.couchbase;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.kv.GetResult;
import com.footballstatsdashboard.api.model.Player;
import com.footballstatsdashboard.db.IPlayerEntityDAO;
import com.footballstatsdashboard.db.key.CouchbaseKeyProvider;
import com.footballstatsdashboard.db.key.ResourceKey;
import io.dropwizard.setup.Environment;

import javax.persistence.EntityNotFoundException;
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
        // TODO: 02/05/22 implement this when the couchbase server is ready
        return false;
    }

    @Override
    public boolean doesEntityExist(UUID entityId) {
        // TODO: 06/05/22 implement this when the couchbase server is ready
        return false;
    }
}