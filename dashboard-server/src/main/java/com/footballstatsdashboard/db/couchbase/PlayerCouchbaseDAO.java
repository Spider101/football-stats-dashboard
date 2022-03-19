package com.footballstatsdashboard.db.couchbase;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.kv.GetResult;
import com.footballstatsdashboard.api.model.Club;
import com.footballstatsdashboard.api.model.Player;
import com.footballstatsdashboard.db.IEntityDAO;
import com.footballstatsdashboard.db.key.CouchbaseKeyProvider;
import com.footballstatsdashboard.db.key.ResourceKey;

import java.util.UUID;

public class PlayerCouchbaseDAO implements IEntityDAO<Player> {
    private final CouchbaseKeyProvider<ResourceKey> keyProvider;
    private final Bucket bucket;

    public PlayerCouchbaseDAO(CouchbaseKeyProvider<ResourceKey> keyProvider,
                              Bucket couchbaseBucket) {
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
        GetResult result = this.bucket.defaultCollection().get(documentKey);
        return result.contentAs(Player.class);
    }

    public void updateEntity(Player updatedEntity) {
        ResourceKey key = new ResourceKey(updatedEntity.getId());
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.bucket.defaultCollection().replace(documentKey, updatedEntity);
    }

    public void deleteEntity(UUID entityId) {
        ResourceKey key = new ResourceKey(entityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.bucket.defaultCollection().remove(documentKey);
    }
}