package com.footballstatsdashboard.db;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.footballstatsdashboard.api.model.MatchPerformance;
import com.footballstatsdashboard.client.couchbase.CouchbaseClientManager;
import com.footballstatsdashboard.db.key.CouchbaseKeyProvider;

import java.util.List;
import java.util.UUID;

public class MatchPerformanceDAO<K> extends CouchbaseDAO<K> {
    private final CouchbaseClientManager.ClusterContainer clusterContainer;

    public MatchPerformanceDAO(CouchbaseClientManager.BucketContainer bucketContainer,
                               CouchbaseClientManager.ClusterContainer clusterContainer,
                               CouchbaseKeyProvider<K> couchbaseKeyProvider) {
        super(bucketContainer, couchbaseKeyProvider);
        this.clusterContainer = clusterContainer;
    }

    public MatchPerformance lookupMatchPerformanceByPlayerId(UUID playerId, UUID competitionId) {
        String query = String.format("Select matchPerformance.* from `%s` matchPerformance" +
                " where playerId = $playerId and competitionId = $competitionId", this.getBucketNameResolver().get());

        QueryOptions queryOptions = QueryOptions.queryOptions().parameters(
                JsonObject.create()
                        .put("playerId", playerId.toString())
                        .put("competitionId", competitionId.toString())
        );
        QueryResult queryResult = this.clusterContainer.getCluster().query(query, queryOptions);
        List<MatchPerformance> matchPerformanceList = queryResult.rowsAs(MatchPerformance.class);
        return matchPerformanceList.stream().findFirst().orElse(null);
    }
}