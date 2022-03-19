package com.footballstatsdashboard.db.couchbase;

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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserCouchbaseDAO implements IUserEntityDAO {
    private final CouchbaseKeyProvider<ResourceKey> keyProvider;
    private final Cluster cluster;
    private final Bucket bucket;
    private final String bucketName;

    public UserCouchbaseDAO(CouchbaseKeyProvider<ResourceKey> keyProvider,
                            Bucket couchbaseBucket, Cluster couchbaseCluster,
                            String bucketName) {
        this.keyProvider = keyProvider;
        this.bucket = couchbaseBucket;
        this.cluster = couchbaseCluster;
        this.bucketName = bucketName;
    }

    public void insertEntity(User entity) {
        ResourceKey key = new ResourceKey(entity.getId());
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.bucket.defaultCollection().insert(documentKey, entity);
    }

    public User getEntity(UUID entityId) {
        ResourceKey key = new ResourceKey(entityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        GetResult result = this.bucket.defaultCollection().get(documentKey);
        return result.contentAs(User.class);
    }

    public void updateEntity(User updatedEntity) {
        ResourceKey key = new ResourceKey(updatedEntity.getId());
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.bucket.defaultCollection().replace(documentKey, updatedEntity);
    }

    public void deleteEntity(UUID entityId) {
        ResourceKey key = new ResourceKey(entityId);
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.bucket.defaultCollection().remove(documentKey);
    }

    public List<User> getExistingUsers(String firstName, String lastName, String emailAddress) {
        String query = "SELECT b.* FROM $bucketName AS b WHERE firstName = $firstName AND lastName = $lastName"
                + "AND email = $email";
        QueryOptions queryOptions = QueryOptions.queryOptions().parameters(
                JsonObject.create()
                        .put("bucketName", this.bucketName)
                        .put("firstName", firstName)
                        .put("lastName", lastName)
                        .put("email", emailAddress)
        );
        QueryResult queryResult = this.cluster.query(query, queryOptions);
        return queryResult.rowsAs(User.class);
    }

    public Optional<User> getUserByEmailAddress(String emailAddress) {
        String query = "SELECT b.* FROM $bucketName AS b WHERE email = $email";
        QueryOptions queryOptions = QueryOptions.queryOptions().parameters(
                JsonObject.create()
                        .put("bucketName", this.bucketName)
                        .put("email", emailAddress)
        );

        QueryResult queryResult = this.cluster.query(query, queryOptions);

        List<User> users = queryResult.rowsAs(User.class);
        if (users.size() == 1) {
            return Optional.of(users.iterator().next());
        }
        // TODO: 30/04/21 figure out a way to handle query result having more than one user
        //  (bad state; should not happen)
        return Optional.empty();
    }
}