package com.footballstatsdashboard.db;

import com.footballstatsdashboard.client.couchbase.CouchbaseClientManager;
import com.footballstatsdashboard.db.key.CouchbaseKeyProvider;

public class CouchbaseDAO<K> {

    private final CouchbaseClientManager.BucketContainer bucketContainer;
    private final CouchbaseKeyProvider<K> keyProvider;

    public CouchbaseDAO(CouchbaseClientManager.BucketContainer bucketContainer,
                        CouchbaseKeyProvider<K> couchbaseKeyProvider) {
        this.bucketContainer = bucketContainer;
        this.keyProvider = couchbaseKeyProvider;
    }

    public CouchbaseClientManager.BucketContainer getBucketContainer() {
        return bucketContainer;
    }

    public CouchbaseKeyProvider<K> getKeyProvider() {
        return keyProvider;
    }

    public void insertDocument(K key, Object document) {
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.bucketContainer.getBucket().defaultCollection().insert(documentKey, document);
    }
}
