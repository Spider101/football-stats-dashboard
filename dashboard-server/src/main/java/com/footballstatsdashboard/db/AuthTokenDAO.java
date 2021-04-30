package com.footballstatsdashboard.db;

import com.footballstatsdashboard.api.model.AuthToken;
import com.footballstatsdashboard.api.model.ImmutableAuthToken;
import com.footballstatsdashboard.client.couchbase.CouchbaseClientManager;
import com.footballstatsdashboard.db.key.CouchbaseKeyProvider;
import org.apache.commons.lang3.tuple.Pair;

import java.time.OffsetDateTime;

public class AuthTokenDAO<K> extends CouchbaseDAO<K> {
    public AuthTokenDAO(CouchbaseClientManager.BucketContainer bucketContainer,
                        CouchbaseKeyProvider<K> couchbaseKeyProvider) {
        super(bucketContainer, couchbaseKeyProvider);
    }

    public void updateLastAccessTime(K authTokenKey, Pair<AuthToken, Long> authTokenEntity) {
        AuthToken updatedAuthToken = ImmutableAuthToken.builder()
                .from(authTokenEntity.getLeft())
                .lastAccessUTC(OffsetDateTime.now())
                .build();

        updateDocument(authTokenKey, updatedAuthToken, authTokenEntity.getRight());
    }
}
