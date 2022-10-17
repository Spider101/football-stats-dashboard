package com.footballstatsdashboard.db.couchbase;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import io.dropwizard.setup.Environment;

import java.util.function.Supplier;

public class CouchbaseDAO {
    private Cluster couchbaseCluster;
    private Bucket couchbaseBucket;

    public Cluster getCouchbaseCluster() {
        return couchbaseCluster;
    }

    public Bucket getCouchbaseBucket() {
        return couchbaseBucket;
    }

    public CouchbaseDAO(Supplier<Cluster> clusterSupplier,
                        Supplier<Bucket> bucketSupplier,
                        Environment environment) {
        environment.lifecycle().addServerLifecycleListener(server -> {
            couchbaseCluster = clusterSupplier.get();
            couchbaseBucket = bucketSupplier.get();
        });
    }
}