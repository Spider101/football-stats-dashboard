package com.footballstatsdashboard.db;

import com.footballstatsdashboard.client.couchbase.CouchbaseClientManager;

public class CouchbaseDAO {

    private final CouchbaseClientManager.BucketContainer bucketContainer;

    public CouchbaseDAO(CouchbaseClientManager.BucketContainer bucketContainer) {
        this.bucketContainer = bucketContainer;
    }

    public void insertDocument(String key, Object document) {
        this.bucketContainer.getBucket().defaultCollection().insert(key, document);
    }
}
