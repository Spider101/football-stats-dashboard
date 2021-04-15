package com.footballstatsdashboard.db;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.kv.GetResult;
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

    public <D> D getDocument(K key, Class<D> clazz) {
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        GetResult result;

        try {
            result = bucketContainer.getBucket().defaultCollection().get(documentKey);
        } catch (DocumentNotFoundException docEx) {
            throw new RuntimeException("Unable to find document with Id: " + documentKey);
        }

        return result.contentAs(clazz);
    }

    public void deleteDocument(K key) {
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        try {
            this.bucketContainer.getBucket().defaultCollection().remove(documentKey);
        } catch (DocumentNotFoundException docEx) {
            throw new RuntimeException("Unable to find document with Id: " + documentKey);
        }
    }
}
