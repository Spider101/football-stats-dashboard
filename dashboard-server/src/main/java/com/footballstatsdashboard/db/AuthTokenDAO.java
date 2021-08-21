package com.footballstatsdashboard.db;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.footballstatsdashboard.api.model.AuthToken;
import com.footballstatsdashboard.api.model.ImmutableAuthToken;
import com.footballstatsdashboard.client.couchbase.CouchbaseClientManager;
import com.footballstatsdashboard.db.key.CouchbaseKeyProvider;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AuthTokenDAO<K> extends CouchbaseDAO<K> {
    private final CouchbaseClientManager.ClusterContainer clusterContainer;
    private final ObjectMapper objectMapper;

    public AuthTokenDAO(CouchbaseClientManager.BucketContainer bucketContainer,
                        CouchbaseClientManager.ClusterContainer clusterContainer,
                        CouchbaseKeyProvider<K> couchbaseKeyProvider,
                        ObjectMapper objectMapper) {
        super(bucketContainer, couchbaseKeyProvider);
        this.clusterContainer = clusterContainer;
        this.objectMapper = objectMapper;
    }

    public void updateLastAccessTime(K authTokenKey, AuthToken authToken) {
        AuthToken updatedAuthToken = ImmutableAuthToken.builder()
                .from(authToken)
                .lastAccessUTC(Instant.now())
                .build();

        updateDocument(authTokenKey, updatedAuthToken);
    }

    public Optional<AuthToken> getAuthTokenForUser(UUID userId) {
        String query = String.format("Select *, META(authToken).cas as cas from `%s` AS authToken " +
                "where userId = $userId", this.getBucketNameResolver().get());
        QueryOptions queryOptions = QueryOptions.queryOptions().parameters(
                JsonObject.create().put("userId", userId.toString())
        );

        QueryResult queryResult = this.clusterContainer.getCluster().query(query, queryOptions);
        List<JsonObject> authTokenEntities = queryResult.rowsAsObject();

        if (authTokenEntities.size() == 1) {
            JsonObject authTokenEntity = authTokenEntities.iterator().next();
            Map<String, Object> authTokenMap = authTokenEntity.getObject("authToken").toMap();
            AuthToken authToken = objectMapper.convertValue(authTokenMap, AuthToken.class);

            return Optional.of(authToken);
        }

        // TODO: 02/05/21 figure out how to handle query result returning more than one auth token for a given user id
        return Optional.empty();
    }
}