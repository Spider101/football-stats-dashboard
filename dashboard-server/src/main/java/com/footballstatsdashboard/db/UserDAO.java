package com.footballstatsdashboard.db;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.client.couchbase.CouchbaseClientManager;
import com.footballstatsdashboard.db.key.CouchbaseKeyProvider;

import java.util.List;
import java.util.Optional;

public class UserDAO<K> extends CouchbaseDAO<K> {

    private final CouchbaseClientManager.ClusterContainer clusterContainer;

    public UserDAO(CouchbaseClientManager.BucketContainer bucketContainer,
                   CouchbaseClientManager.ClusterContainer clusterContainer,
                   CouchbaseKeyProvider<K> couchbaseKeyProvider) {
        super(bucketContainer, couchbaseKeyProvider);
        this.clusterContainer = clusterContainer;
    }

    public List<User> getUsersByFirstNameLastNameEmail(String firstName, String lastName, String emailAddress) {
        String query = "Select b.* from `dashboard-server` AS b where firstName = $firstName AND lastName = $lastName" +
                " AND email = $email";
        QueryOptions queryOptions = QueryOptions.queryOptions().parameters(
                JsonObject.create()
                        .put("firstName", firstName)
                        .put("lastName", lastName)
                        .put("email", emailAddress)
        );
        QueryResult queryResult = this.clusterContainer.getCluster().query(query, queryOptions);
        return queryResult.rowsAs(User.class);
    }

    public Optional<User> getUserByCredentials(String emailAddress) {
        String query = "Select b.* from `dashboard-server` AS b where email = $email";
        QueryOptions queryOptions = QueryOptions.queryOptions().parameters(
                JsonObject.create().put("email", emailAddress)
        );

        QueryResult queryResult = this.clusterContainer.getCluster().query(query, queryOptions);

        List<User> users = queryResult.rowsAs(User.class);
        if (users.size() == 1) {
            return Optional.of(users.iterator().next());
        }
        // TODO: 30/04/21 figure out a way to handle query result having more than one user (bad state; should not happen)
        return Optional.empty();
    }
}
