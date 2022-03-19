package com.footballstatsdashboard.db.couchbase;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.footballstatsdashboard.api.model.AuthToken;
import com.footballstatsdashboard.db.IAuthTokenEntityDAO;
import com.footballstatsdashboard.db.key.CouchbaseKeyProvider;
import com.footballstatsdashboard.db.key.ResourceKey;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthTokenCouchbaseDAO implements IAuthTokenEntityDAO {
    private final CouchbaseKeyProvider<ResourceKey> keyProvider;
    private final Cluster cluster;
    private final Bucket bucket;
    private final String bucketName;
    public AuthTokenCouchbaseDAO(CouchbaseKeyProvider<ResourceKey> keyProvider,
                                 Cluster couchbaseCluster, Bucket couchbaseBucket,
                                 String bucketName) {
        this.keyProvider = keyProvider;
        this.cluster = couchbaseCluster;
        this.bucket = couchbaseBucket;
        this.bucketName = bucketName;
    }

    public void insertEntity(AuthToken entity) {
        ResourceKey key = new ResourceKey(entity.getId());
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.bucket.defaultCollection().insert(documentKey, entity);
    }

    public AuthToken getEntity(UUID entityId) {
        ResourceKey key = new ResourceKey(entityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        GetResult result = this.bucket.defaultCollection().get(documentKey);
        return result.contentAs(AuthToken.class);
    }

    public void updateEntity(AuthToken updatedEntity) {
        ResourceKey key = new ResourceKey(updatedEntity.getId());
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.bucket.defaultCollection().replace(documentKey, updatedEntity);
    }

    public void deleteEntity(UUID entityId) {
        ResourceKey key = new ResourceKey(entityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.bucket.defaultCollection().remove(documentKey);
    }

    public Optional<AuthToken> getAuthTokenForUser(UUID userId) {
        String query = "SELECT *, META(authToken).cas AS cas FROM $bucketName AS authToken " +
                "where userId = $userId";
        QueryOptions queryOptions = QueryOptions.queryOptions().parameters(
                JsonObject.create()
                        .put("bucketName", this.bucketName)
                        .put("userId", userId.toString())
        );

        QueryResult queryResult = this.cluster.query(query, queryOptions);
        // TODO: 18/03/22 figure out why we can't directly convert the query result to auth token
        List<AuthToken> authTokens = queryResult.rowsAs(AuthToken.class);
        if (authTokens.size() == 1) {
            return Optional.of(authTokens.get(0));
        }
//        List<JsonObject> authTokenEntities = queryResult.rowsAsObject();
//
//        if (authTokenEntities.size() == 1) {
//            JsonObject authTokenEntity = authTokenEntities.iterator().next();
//            Map<String, Object> authTokenMap = authTokenEntity.getObject("authToken").toMap();
//            AuthToken authToken = objectMapper.convertValue(authTokenMap, AuthToken.class);
//
//            return Optional.of(authToken);
//        }

        // TODO: 02/05/21 figure out how to handle query result returning more than one auth token for a given user id
        return Optional.empty();
    }
}