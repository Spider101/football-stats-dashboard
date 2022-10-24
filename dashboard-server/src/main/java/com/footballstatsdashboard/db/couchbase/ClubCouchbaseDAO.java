package com.footballstatsdashboard.db.couchbase;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.ExistsResult;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.LookupInResult;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.footballstatsdashboard.api.model.Club;
import com.footballstatsdashboard.api.model.club.ClubSummary;
import com.footballstatsdashboard.api.model.club.ImmutableSquadPlayer;
import com.footballstatsdashboard.api.model.club.SquadPlayer;
import com.footballstatsdashboard.db.IClubEntityDAO;
import com.footballstatsdashboard.db.key.CouchbaseKeyProvider;
import com.footballstatsdashboard.db.key.ResourceKey;
import io.dropwizard.setup.Environment;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.couchbase.client.java.kv.LookupInSpec.get;

public class ClubCouchbaseDAO extends CouchbaseDAO implements IClubEntityDAO {
    private final CouchbaseKeyProvider<ResourceKey> keyProvider;
    private static final int MATCH_LIMIT_FOR_FORM = 5;

    public ClubCouchbaseDAO(CouchbaseKeyProvider<ResourceKey> keyProvider,
                            Supplier<Cluster> clusterSupplier,
                            Supplier<Bucket> bucketSupplier,
                            Environment environment) {
        super(clusterSupplier, bucketSupplier, environment);
        this.keyProvider = keyProvider;
    }

    @Override
    public void insertEntity(Club entity) {
        ResourceKey key = new ResourceKey(entity.getId());
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.getCouchbaseBucket().defaultCollection().insert(documentKey, entity);
    }

    @Override
    public Club getEntity(UUID entityId) {
        ResourceKey key = new ResourceKey(entityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        try {
            GetResult result = this.getCouchbaseBucket().defaultCollection().get(documentKey);
            return result.contentAs(Club.class);
        } catch (DocumentNotFoundException documentNotFoundException) {
            throw new EntityNotFoundException(documentNotFoundException.getMessage());
        }
    }

    @Override
    public void updateEntity(UUID existingEntityId, Club updatedEntity) {
        ResourceKey key = new ResourceKey(existingEntityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.getCouchbaseBucket().defaultCollection().replace(documentKey, updatedEntity);
    }

    @Override
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
        ResourceKey key = new ResourceKey(entityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        LookupInResult lookupInResult;

        try {
            lookupInResult = this.getCouchbaseBucket().defaultCollection()
                    .lookupIn(documentKey, Collections.singletonList(get("userId")));
        } catch (DocumentNotFoundException documentNotFoundException) {
            throw new EntityNotFoundException(documentNotFoundException.getMessage());
        }

        UUID userIdAssociatedWithClub = lookupInResult.contentAs(0, UUID.class);
        return userIdAssociatedWithClub.equals(userId);
    }

    @Override
    public boolean doesEntityExist(UUID entityId) {
        ResourceKey key = new ResourceKey(entityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        ExistsResult result = this.getCouchbaseBucket().defaultCollection().exists(documentKey);
        return result.exists();
    }

    @Override
    public List<ClubSummary> getClubSummariesForUser(UUID userId) {
        String query = String.format("SELECT club.id AS clubId, club.name, club.logo, club.createdDate FROM `%s` club" +
                " WHERE club.type = 'Club' AND club.userId = $userId", this.getCouchbaseBucket().name());
        QueryOptions queryOptions = QueryOptions.queryOptions().parameters(
                JsonObject.create().put("userId", userId.toString())
        );
        QueryResult queryResult = this.getCouchbaseCluster().query(query, queryOptions);
        return queryResult.rowsAs(ClubSummary.class);
    }

    @Override
    public List<SquadPlayer> getPlayersInClub(UUID clubId) {
        String query = String.format("SELECT player.metadata.name, player.metadata.country, player.id," +
                " player.metadata.countryLogo AS countryFlag, player.ability.`current` AS currentAbility," +
                " player.roles, matchPerformance.matchRating.history AS matchRatingHistory" +
                " FROM `%s` player LEFT JOIN `%s` matchPerformance" +
                " ON player.id = matchPerformance.playerId" +
                " WHERE player.type = 'Player' AND player.clubId = $clubId",
                this.getCouchbaseBucket().name(), this.getCouchbaseBucket().name());

        QueryOptions queryOptions = QueryOptions.queryOptions().parameters(
                JsonObject.create().put("clubId", clubId.toString())
        );
        QueryResult queryResult = this.getCouchbaseCluster().query(query, queryOptions);
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