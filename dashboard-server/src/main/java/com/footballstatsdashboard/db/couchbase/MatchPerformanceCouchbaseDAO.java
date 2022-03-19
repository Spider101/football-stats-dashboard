package com.footballstatsdashboard.db.couchbase;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.footballstatsdashboard.api.model.MatchPerformance;
import com.footballstatsdashboard.core.exceptions.EntityNotFoundException;
import com.footballstatsdashboard.db.IMatchPerformanceEntityDAO;
import com.footballstatsdashboard.db.key.CouchbaseKeyProvider;
import com.footballstatsdashboard.db.key.ResourceKey;

import java.util.List;
import java.util.UUID;

public class MatchPerformanceCouchbaseDAO implements IMatchPerformanceEntityDAO {
    private final CouchbaseKeyProvider<ResourceKey> keyProvider;
    private final Cluster cluster;
    private final Bucket bucket;
    private final String bucketName;

    public MatchPerformanceCouchbaseDAO(CouchbaseKeyProvider<ResourceKey> keyProvider,
                                        Cluster couchbaseCluster, Bucket couchbaseBucket,
                                        String bucketName) {
        this.keyProvider = keyProvider;
        this.cluster = couchbaseCluster;
        this.bucket = couchbaseBucket;
        this.bucketName = bucketName;
    }

    public void insertEntity(MatchPerformance entity) {
        ResourceKey key = new ResourceKey(entity.getId());
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.bucket.defaultCollection().insert(documentKey, entity);
    }

    public MatchPerformance getEntity(UUID entityId) {
        ResourceKey key = new ResourceKey(entityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        try {
            GetResult result = this.bucket.defaultCollection().get(documentKey);
            return result.contentAs(MatchPerformance.class);
        } catch (DocumentNotFoundException documentNotFoundException) {
            throw new EntityNotFoundException(documentNotFoundException.getMessage());
        }
    }

    public void updateEntity(UUID existingEntityId, MatchPerformance updatedEntity) {
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

    public List<MatchPerformance> getMatchPerformanceOfPlayerInCompetition(UUID playerId, UUID competitionId) {
        String query = "Select matchPerformance.* from $bucketName matchPerformance" +
                " where playerId = $playerId and competitionId = $competitionId";

        QueryOptions queryOptions = QueryOptions.queryOptions().parameters(
                JsonObject.create()
                        .put("bucketName", this.bucketName)
                        .put("playerId", playerId.toString())
                        .put("competitionId", competitionId.toString())
        );
        QueryResult queryResult = this.cluster.query(query, queryOptions);
        return queryResult.rowsAs(MatchPerformance.class);
    }
}