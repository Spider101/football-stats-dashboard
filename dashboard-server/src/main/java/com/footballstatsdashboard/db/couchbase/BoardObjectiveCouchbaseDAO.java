package com.footballstatsdashboard.db.couchbase;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.LookupInResult;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.footballstatsdashboard.api.model.club.BoardObjective;
import com.footballstatsdashboard.db.IBoardObjectiveEntityDAO;
import com.footballstatsdashboard.db.key.CouchbaseKeyProvider;
import com.footballstatsdashboard.db.key.ResourceKey;
import io.dropwizard.setup.Environment;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static com.couchbase.client.java.kv.LookupInSpec.get;

public class BoardObjectiveCouchbaseDAO extends CouchbaseDAO implements IBoardObjectiveEntityDAO {
    private final CouchbaseKeyProvider<ResourceKey> keyProvider;

    public BoardObjectiveCouchbaseDAO(CouchbaseKeyProvider<ResourceKey> keyProvider,
                                        Supplier<Cluster> clusterSupplier,
                                        Supplier<Bucket> bucketSupplier,
                                        Environment environment) {
        super(clusterSupplier, bucketSupplier, environment);
        this.keyProvider = keyProvider;
    }
    @Override
    public void insertEntity(BoardObjective entity) {
        ResourceKey key = new ResourceKey(entity.getId());
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.getCouchbaseBucket().defaultCollection().insert(documentKey, entity);
    }

    @Override
    public BoardObjective getEntity(UUID entityId) throws EntityNotFoundException {
        ResourceKey key = new ResourceKey(entityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        try {
            GetResult result = this.getCouchbaseBucket().defaultCollection().get(documentKey);
            return result.contentAs(BoardObjective.class);
        } catch (DocumentNotFoundException documentNotFoundException) {
            throw new EntityNotFoundException(documentNotFoundException.getMessage());
        }
    }

    @Override
    public void updateEntity(UUID existingEntityId, BoardObjective updatedEntity) {
        ResourceKey key = new ResourceKey(existingEntityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.getCouchbaseBucket().defaultCollection().replace(documentKey, updatedEntity);
    }

    @Override
    public void deleteEntity(UUID entityId) throws EntityNotFoundException {
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
        String bucketName = this.getCouchbaseBucket().name();
        String query = String.format("SELECT club.userId FROM `%s` AS boardObjective JOIN `%s` AS club" +
                " ON boardObjective.clubId = club.id" +
                " WHERE boardObjective.type = 'BoardObjective' AND club.type = 'Club'" +
                " AND boardObjective.id = $boardObjectiveId", bucketName, bucketName);

        QueryOptions queryOptions = QueryOptions.queryOptions().parameters(
                JsonObject.create().put("boardObjectiveId", entityId.toString())
        );
        QueryResult queryResult = this.getCouchbaseCluster().query(query, queryOptions);
        List<JsonObject> resultRows = queryResult.rowsAsObject();

        if (resultRows.size() == 1) {
            UUID userIdAssociatedWithBoardObjective = UUID.fromString(resultRows.get(0).getString("userId"));
            return userIdAssociatedWithBoardObjective.equals(userId);
        }

        // TODO: 30/04/21 figure out a way to handle query result having more than one userId
        //  (bad state; should not happen)
        return false;
    }

    @Override
    public boolean doesEntityBelongToClub(UUID entityId, UUID clubId) {
        ResourceKey key = new ResourceKey(entityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        LookupInResult lookupInResult;

        try {
            lookupInResult = this.getCouchbaseBucket().defaultCollection()
                    .lookupIn(documentKey, Collections.singletonList(get("clubId")));
        } catch (DocumentNotFoundException documentNotFoundException) {
            throw new EntityNotFoundException(documentNotFoundException.getMessage());
        }

        UUID clubIdAssociatedWithBoardObjective = lookupInResult.contentAs(0, UUID.class);
        return clubIdAssociatedWithBoardObjective.equals(clubId);
    }

    @Override
    public List<BoardObjective> getBoardObjectivesForClub(UUID clubId) {
        String query = String.format("SELECT boardObjective.* FROM `%s` AS boardObjective" +
                        " WHERE boardObjective.type = 'BoardObjective' AND boardObjective.clubId = $clubId",
                this.getCouchbaseBucket().name());
        QueryOptions queryOptions = QueryOptions.queryOptions().parameters(
                JsonObject.create().put("clubId", clubId.toString())
        );
        QueryResult queryResult = this.getCouchbaseCluster().query(query, queryOptions);
        return queryResult.rowsAs(BoardObjective.class);
    }
}