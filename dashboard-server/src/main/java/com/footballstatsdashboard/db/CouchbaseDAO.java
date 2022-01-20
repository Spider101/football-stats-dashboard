package com.footballstatsdashboard.db;

import com.couchbase.client.java.kv.GetResult;
import com.footballstatsdashboard.client.couchbase.CouchbaseClientManager;
import com.footballstatsdashboard.db.key.CouchbaseKeyProvider;

import java.util.function.Supplier;

public class CouchbaseDAO<K> {

    private final CouchbaseClientManager.BucketContainer bucketContainer;
    private final CouchbaseKeyProvider<K> keyProvider;
    private final Supplier<String> bucketNameResolver;

    public CouchbaseDAO(CouchbaseClientManager.BucketContainer bucketContainer,
                        CouchbaseKeyProvider<K> couchbaseKeyProvider) {
        this.bucketContainer = bucketContainer;
        this.keyProvider = couchbaseKeyProvider;
        this.bucketNameResolver = () -> bucketContainer.getBucket().name();
    }

    public CouchbaseClientManager.BucketContainer getBucketContainer() {
        return bucketContainer;
    }

    public CouchbaseKeyProvider<K> getKeyProvider() {
        return keyProvider;
    }

    public Supplier<String> getBucketNameResolver() {
        return bucketNameResolver;
    }

    public void insertDocument(K key, Object document) {
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.bucketContainer.getBucket().defaultCollection().insert(documentKey, document);
    }

    public <D> void updateDocument(K key, D document) {
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.bucketContainer.getBucket().defaultCollection().replace(documentKey, document);
    }

    public <D> D getDocument(K key, Class<D> clazz) {
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        GetResult result = bucketContainer.getBucket().defaultCollection().get(documentKey);
        return result.contentAs(clazz);
    }

    public void deleteDocument(K key) {
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.bucketContainer.getBucket().defaultCollection().remove(documentKey);
    }
}