package com.footballstatsdashboard.db;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.footballstatsdashboard.api.model.club.ImmutableSquadPlayer;
import com.footballstatsdashboard.api.model.club.SquadPlayer;
import com.footballstatsdashboard.client.couchbase.CouchbaseClientManager;
import com.footballstatsdashboard.db.key.CouchbaseKeyProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClubDAO<K> extends CouchbaseDAO<K> {
    private final CouchbaseClientManager.ClusterContainer clusterContainer;

    public ClubDAO(CouchbaseClientManager.BucketContainer bucketContainer,
                   CouchbaseClientManager.ClusterContainer clusterContainer,
                   CouchbaseKeyProvider<K> couchbaseKeyProvider) {
        super(bucketContainer, couchbaseKeyProvider);
        this.clusterContainer = clusterContainer;
    }

    public List<SquadPlayer> getPlayersInClub(UUID clubId) {
        String query = String.format("Select player.metadata.name, player.metadata.country, player.id," +
                        " player.ability.`current` as currentAbility, player.roles," +
                        " matchPerformance.matchRating.history as matchRatingHistory" +
                        " from `%s` player left join `%s` matchPerformance on player.id = matchPerformance.playerId" +
                        " where player.type = 'Player' and player.clubId = $clubId",
                this.getBucketNameResolver().get(), this.getBucketNameResolver().get());

        QueryOptions queryOptions = QueryOptions.queryOptions().parameters(
                JsonObject.create().put("clubId", clubId.toString())
        );
        QueryResult queryResult = this.clusterContainer.getCluster().query(query, queryOptions);
        List<JsonObject> resultRows = queryResult.rowsAsObject();

        return resultRows.stream().map(row -> {
            JsonArray roles = (JsonArray) row.get("roles");

            JsonArray matchRatingHistory = (JsonArray) row.get("matchRatingHistory");
            List<Float> recentForm = matchRatingHistory != null
                    ? matchRatingHistory.toList().stream()
                    .map(rating -> Float.valueOf(rating.toString()))
                    // recent form is limited to the last 5 matches
                    .limit(Math.min(5, matchRatingHistory.size()))
                    .collect(Collectors.toList())
                    : new ArrayList<>();

            return ImmutableSquadPlayer.builder()
                    .name(row.get("name").toString())
                    .country(row.get("country").toString())
                    .currentAbility((int) row.get("currentAbility"))
                    .recentForm(recentForm)
                    .role(roles.getObject(0).getString("name"))
                    .playerId(UUID.fromString(row.get("id").toString()))
                    .build();
        }).collect(Collectors.toList());
    }
}