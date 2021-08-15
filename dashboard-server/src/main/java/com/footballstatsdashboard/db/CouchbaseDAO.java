package com.footballstatsdashboard.db;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.ReplaceOptions;
import com.footballstatsdashboard.client.couchbase.CouchbaseClientManager;
import com.footballstatsdashboard.db.key.CouchbaseKeyProvider;
import org.apache.commons.lang3.tuple.Pair;

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

    public <D> void updateDocument(K key, D document, Long casValue) {
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        this.bucketContainer.getBucket().defaultCollection().replace(documentKey, document,
                ReplaceOptions.replaceOptions().cas(casValue));
    }

    public <D> Pair<D, Long> getDocument(K key, Class<D> clazz) {
        String documentKey = this.keyProvider.getCouchbaseKey(key);
        GetResult result;

        try {
            result = bucketContainer.getBucket().defaultCollection().get(documentKey);
        } catch (DocumentNotFoundException docEx) {
            throw new RuntimeException("Unable to find document with Id: " + documentKey);
        }

        return Pair.of(result.contentAs(clazz), result.cas());
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