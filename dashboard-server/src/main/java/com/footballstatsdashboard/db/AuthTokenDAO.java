package com.footballstatsdashboard.db;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.footballstatsdashboard.api.model.AuthToken;
import com.footballstatsdashboard.api.model.ImmutableAuthToken;
import com.footballstatsdashboard.client.couchbase.CouchbaseClientManager;
import com.footballstatsdashboard.db.key.CouchbaseKeyProvider;
import org.apache.commons.lang3.tuple.Pair;

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

    public void updateLastAccessTime(K authTokenKey, Pair<AuthToken, Long> authTokenEntity) {
        AuthToken updatedAuthToken = ImmutableAuthToken.builder()
                .from(authTokenEntity.getLeft())
                .lastAccessUTC(Instant.now())
                .build();

        updateDocument(authTokenKey, updatedAuthToken, authTokenEntity.getRight());
    }

    public Optional<Pair<AuthToken, Long>> getAuthTokenForUser(UUID userId) {
        String query = "Select *, META(authToken).cas as cas from `dashboard-server` AS authToken " +
                "where userId = $userId";
        QueryOptions queryOptions = QueryOptions.queryOptions().parameters(
                JsonObject.create().put("userId", userId.toString())
        );

        QueryResult queryResult = this.clusterContainer.getCluster().query(query, queryOptions);
        List<JsonObject> authTokenEntities = queryResult.rowsAsObject();

        if (authTokenEntities.size() == 1) {
            JsonObject authTokenEntity = authTokenEntities.iterator().next();
            Long cas = authTokenEntity.getLong("cas");
            Map<String, Object> authTokenMap = authTokenEntity.getObject("authToken").toMap();
            AuthToken authToken = objectMapper.convertValue(authTokenMap, AuthToken.class);

            return Optional.of(Pair.of(authToken, cas));
        }

//        List<AuthToken> authTokenList = queryResult.rowsAs(AuthToken.class);

//        if (authTokenList.size() == 1) {
//            return Optional.of(authTokenList.iterator().next());
//        }

        // TODO: 02/05/21 figure out how to handle query result returning more than one auth token for a given user id
        return Optional.empty();
    }
}
