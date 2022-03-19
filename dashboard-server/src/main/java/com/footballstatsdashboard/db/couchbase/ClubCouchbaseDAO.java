package com.footballstatsdashboard.db.couchbase;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.footballstatsdashboard.api.model.Club;
import com.footballstatsdashboard.api.model.club.ClubSummary;
import com.footballstatsdashboard.api.model.club.ImmutableSquadPlayer;
import com.footballstatsdashboard.api.model.club.SquadPlayer;
import com.footballstatsdashboard.core.exceptions.EntityNotFoundException;
import com.footballstatsdashboard.db.IClubEntityDAO;
import com.footballstatsdashboard.db.key.CouchbaseKeyProvider;
import com.footballstatsdashboard.db.key.ResourceKey;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClubCouchbaseDAO implements IClubEntityDAO {
    private final CouchbaseKeyProvider<ResourceKey> keyProvider;
    private final Cluster cluster;
    private final Bucket bucket;
    private final String bucketName;
    private static final int MATCH_LIMIT_FOR_FORM = 5;

    public ClubCouchbaseDAO(CouchbaseKeyProvider<ResourceKey> keyProvider,
                            Cluster couchbaseCluster, Bucket couchbaseBucket,
                            String bucketName) {

        this.keyProvider = keyProvider;
        this.cluster = couchbaseCluster;
        this.bucket = couchbaseBucket;
        this.bucketName = bucketName;
    }

    public void insertEntity(Club entity) {
        ResourceKey key = new ResourceKey(entity.getId());
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.bucket.defaultCollection().insert(documentKey, entity);
    }

    public Club getEntity(UUID entityId) {
        ResourceKey key = new ResourceKey(entityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        try {
            GetResult result = this.bucket.defaultCollection().get(documentKey);
            return result.contentAs(Club.class);
        } catch (DocumentNotFoundException documentNotFoundException) {
            throw new EntityNotFoundException(documentNotFoundException.getMessage());
        }
    }

    public void updateEntity(UUID existingEntityId, Club updatedEntity) {
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

    public List<ClubSummary> getClubSummariesForUser(UUID userId) {
        // TODO: 04/03/22 grab the club logo file key from the club document as well
        String query = "Select club.id as clubId, club.name, club.createdDate from $bucketName club" +
                " where club.type = 'Club' and club.userId = $userId";
        QueryOptions queryOptions = QueryOptions.queryOptions().parameters(
                JsonObject.create()
                        .put("bucketName", this.bucketName)
                        .put("userId", userId.toString())
        );
        QueryResult queryResult = this.cluster.query(query, queryOptions);
        return queryResult.rowsAs(ClubSummary.class);
    }

    public List<SquadPlayer> getPlayersInClub(UUID clubId) {
        String query = "Select player.metadata.name, player.metadata.country, player.id," +
                " player.metadata.countryLogo as countryFlag, player.ability.`current` as currentAbility," +
                " player.roles, matchPerformance.matchRating.history as matchRatingHistory" +
                " from $bucketName player left join $bucketName matchPerformance" +
                "on player.id = matchPerformance.playerId" +
                " where player.type = 'Player' and player.clubId = $clubId";

        QueryOptions queryOptions = QueryOptions.queryOptions().parameters(
                JsonObject.create()
                        .put("clubId", clubId.toString())
                        .put("bucketName", this.bucketName)
        );
        QueryResult queryResult = this.cluster.query(query, queryOptions);
        List<JsonObject> resultRows = queryResult.rowsAsObject();

        return resultRows.stream().map(row -> {
            JsonArray roles = (JsonArray) row.get("roles");

            JsonArray matchRatingHistory = (JsonArray) row.get("matchRatingHistory");
            List<Float> recentForm = matchRatingHistory != null
                    ? matchRatingHistory.toList().stream()
                    .map(rating -> Float.valueOf(rating.toString()))
                    // recent form is limited to the last 5 matches
                    .limit(Math.min(MATCH_LIMIT_FOR_FORM, matchRatingHistory.size()))
                    .collect(Collectors.toList())
                    : new ArrayList<>();

            return ImmutableSquadPlayer.builder()
                    .name(row.get("name").toString())
                    .country(row.get("country").toString())
                    .countryFlag(row.get("countryFlag").toString())
                    .currentAbility((int) row.get("currentAbility"))
                    .recentForm(recentForm)
                    .role(roles.getObject(0).getString("name"))
                    .playerId(UUID.fromString(row.get("id").toString()))
                    .build();
        }).collect(Collectors.toList());
    }
}