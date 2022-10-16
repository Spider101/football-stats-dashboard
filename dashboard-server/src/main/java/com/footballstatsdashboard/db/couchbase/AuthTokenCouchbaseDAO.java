package com.footballstatsdashboard.db.couchbase;

import com.couchbase.client.core.error.DocumentNotFoundException;
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
import io.dropwizard.setup.Environment;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class AuthTokenCouchbaseDAO extends CouchbaseDAO implements IAuthTokenEntityDAO {
    private final CouchbaseKeyProvider<ResourceKey> keyProvider;

    public AuthTokenCouchbaseDAO(CouchbaseKeyProvider<ResourceKey> keyProvider,
                                    Supplier<Cluster> clusterSupplier,
                                    Supplier<Bucket> bucketSupplier,
                                    Environment environment) {
        super(clusterSupplier, bucketSupplier, environment);
        this.keyProvider = keyProvider;
    }

    public void insertEntity(AuthToken entity) {
        ResourceKey key = new ResourceKey(entity.getId());
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.getCouchbaseBucket().defaultCollection().insert(documentKey, entity);
    }

    public AuthToken getEntity(UUID entityId) {
        ResourceKey key = new ResourceKey(entityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        try {
            GetResult result = this.getCouchbaseBucket().defaultCollection().get(documentKey);
            return result.contentAs(AuthToken.class);
        } catch (DocumentNotFoundException documentNotFoundException) {
            throw new EntityNotFoundException(documentNotFoundException.getMessage());
        }
    }

    public void updateEntity(UUID existingEntityId, AuthToken updatedEntity) {
        ResourceKey key = new ResourceKey(existingEntityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.getCouchbaseBucket().defaultCollection().replace(documentKey, updatedEntity);
    }

    public void deleteEntity(UUID entityId) {
        ResourceKey key = new ResourceKey(entityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        try {
            this.getCouchbaseBucket().defaultCollection().remove(documentKey);
        } catch (DocumentNotFoundException documentNotFoundException) {
            throw new EntityNotFoundException(documentNotFoundException.getMessage());
        }
    }

    public Optional<AuthToken> getAuthTokenForUser(UUID userId) {
        String query = String.format("SELECT authToken.* FROM `%s` AS authToken WHERE userId = $userId",
                this.getCouchbaseBucket().name());
        QueryOptions queryOptions = QueryOptions.queryOptions().parameters(
                JsonObject.create().put("userId", userId.toString())
        );

        QueryResult queryResult = this.getCouchbaseCluster().query(query, queryOptions);
        List<AuthToken> authTokens = queryResult.rowsAs(AuthToken.class);
        if (authTokens.size() == 1) {
            return Optional.of(authTokens.get(0));
        }

        // TODO: 02/05/21 figure out how to handle query result returning more than one auth token for a given user id
        return Optional.empty();
    }
}