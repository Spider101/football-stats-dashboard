package com.footballstatsdashboard.db.couchbase;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.kv.GetResult;
import com.footballstatsdashboard.api.model.Player;
import com.footballstatsdashboard.db.IPlayerEntityDAO;
import com.footballstatsdashboard.db.key.CouchbaseKeyProvider;
import com.footballstatsdashboard.db.key.ResourceKey;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

public class PlayerCouchbaseDAO implements IPlayerEntityDAO {
    private final CouchbaseKeyProvider<ResourceKey> keyProvider;
    private final Bucket bucket;

    public PlayerCouchbaseDAO(CouchbaseKeyProvider<ResourceKey> keyProvider, Bucket couchbaseBucket) {
        this.keyProvider = keyProvider;
        this.bucket = couchbaseBucket;
    }

    public void insertEntity(Player entity) {
        ResourceKey key = new ResourceKey(entity.getId());
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.bucket.defaultCollection().insert(documentKey, entity);
    }

    public Player getEntity(UUID entityId) {
        ResourceKey key = new ResourceKey(entityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        try {
            GetResult result = this.bucket.defaultCollection().get(documentKey);
            return result.contentAs(Player.class);
        } catch (DocumentNotFoundException documentNotFoundException) {
            throw new EntityNotFoundException(documentNotFoundException.getMessage());
        }
    }

    public void updateEntity(UUID existingEntityId, Player updatedEntity) {
        ResourceKey key = new ResourceKey(existingEntityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.bucket.defaultCollection().replace(documentKey, updatedEntity);
    }

    public void deleteEntity(UUID entityId) {
        ResourceKey key = new ResourceKey(entityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        try {
            this.bucket.defaultCollection().remove(documentKey);
        } catch (DocumentNotFoundException documentNotFoundException) {
            throw new EntityNotFoundException(documentNotFoundException.getMessage());
        }
    }
}