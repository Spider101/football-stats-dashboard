package com.footballstatsdashboard.db.couchbase;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.db.IUserEntityDAO;
import com.footballstatsdashboard.db.key.CouchbaseKeyProvider;
import com.footballstatsdashboard.db.key.ResourceKey;
import io.dropwizard.setup.Environment;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class UserCouchbaseDAO extends CouchbaseDAO implements IUserEntityDAO {
    private final CouchbaseKeyProvider<ResourceKey> keyProvider;

    public UserCouchbaseDAO(CouchbaseKeyProvider<ResourceKey> keyProvider,
                            Supplier<Cluster> clusterSupplier,
                            Supplier<Bucket> bucketSupplier,
                            Environment environment) {
        super(clusterSupplier, bucketSupplier, environment);
        this.keyProvider = keyProvider;
    }

    public void insertEntity(User entity) {
        ResourceKey key = new ResourceKey(entity.getId());
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.getCouchbaseBucket().defaultCollection().insert(documentKey, entity);
    }

    public User getEntity(UUID entityId) {
        ResourceKey key = new ResourceKey(entityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        try {
            GetResult result = this.getCouchbaseBucket().defaultCollection().get(documentKey);
            return result.contentAs(User.class);
        } catch (DocumentNotFoundException documentNotFoundException) {
            throw new EntityNotFoundException(documentNotFoundException.getMessage());
        }
    }

    public void updateEntity(UUID existingEntityId, User updatedEntity) {
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

    public List<User> getExistingUsers(String firstName, String lastName, String emailAddress) {
        String query = "SELECT b.* FROM $bucketName AS b WHERE firstName = $firstName AND lastName = $lastName"
                + "AND email = $email";
        QueryOptions queryOptions = QueryOptions.queryOptions().parameters(
                JsonObject.create()
                        .put("bucketName", this.getCouchbaseBucket().name())
                        .put("firstName", firstName)
                        .put("lastName", lastName)
                        .put("email", emailAddress)
        );
        QueryResult queryResult = this.getCouchbaseCluster().query(query, queryOptions);
        return queryResult.rowsAs(User.class);
    }

    public Optional<User> getUserByEmailAddress(String emailAddress) {
        String query = "SELECT b.* FROM $bucketName AS b WHERE email = $email";
        QueryOptions queryOptions = QueryOptions.queryOptions().parameters(
                JsonObject.create()
                        .put("bucketName", this.getCouchbaseBucket().name())
                        .put("email", emailAddress)
        );

        QueryResult queryResult = this.getCouchbaseCluster().query(query, queryOptions);

        List<User> users = queryResult.rowsAs(User.class);
        if (users.size() == 1) {
            return Optional.of(users.iterator().next());
        }
        // TODO: 30/04/21 figure out a way to handle query result having more than one user
        //  (bad state; should not happen)
        return Optional.empty();
    }
}