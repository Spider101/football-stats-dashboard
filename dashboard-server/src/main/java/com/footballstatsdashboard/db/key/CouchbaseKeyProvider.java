package com.footballstatsdashboard.db.key;

public interface CouchbaseKeyProvider<K> {
    String getCouchbaseKey(K key);
}
