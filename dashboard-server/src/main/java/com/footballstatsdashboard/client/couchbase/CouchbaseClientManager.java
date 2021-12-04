package com.footballstatsdashboard.client.couchbase;

import com.couchbase.client.core.env.TimeoutConfig;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.client.java.env.ClusterEnvironment;
import com.footballstatsdashboard.client.couchbase.config.ClusterConfiguration;
import com.footballstatsdashboard.client.couchbase.config.CouchbaseClientConfiguration;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class CouchbaseClientManager implements Managed {

    private static final Logger LOGGER = LoggerFactory.getLogger(CouchbaseClientManager.class);

    private final String appName;
    private final Environment environment;

    private final Map<String, ClusterContainer> clusterContainers;
    private final ClusterEnvironment defaultClusterEnvironment;
    private final CouchbaseClientConfiguration config;

    public CouchbaseClientManager(String appName, Environment environment, CouchbaseClientConfiguration config) {
        this.appName = appName;
        this.environment = environment;
        this.config = config;
        this.clusterContainers = new HashMap<>();
        this.defaultClusterEnvironment = ClusterEnvironment.builder()
                .timeoutConfig(TimeoutConfig.kvTimeout(Duration.ofMillis(config.getKvTimeout())))
                .build();

        this.config.getClusters().forEach((clusterName, clusterConfiguration) -> {
            ClusterContainer clusterContainer = new ClusterContainer();
            clusterContainer.setBucketContainers(initializeBucketContainers(clusterConfiguration));
            this.clusterContainers.put(clusterName, clusterContainer);
        });
    }

    private Map<String, BucketContainer> initializeBucketContainers(ClusterConfiguration clusterConfiguration) {
        Map<String, BucketContainer> bucketContainers = new HashMap<>();
        clusterConfiguration.getBuckets().forEach(bucketName -> {
            bucketContainers.put(bucketName, new BucketContainer());
        });
        return bucketContainers;
    }

    public String getAppName() {
        return appName;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void start() {
        LOGGER.info("Connecting to couchbase cluster: {}", "football-stats-dashboard");

        this.config.getClusters().forEach((clusterName, clusterConfig) -> {
            String username = clusterConfig.getUsername();
            String password = clusterConfig.getPassword();
            ClusterOptions clusterOptions = ClusterOptions.clusterOptions(username, password)
                    .environment(defaultClusterEnvironment);
            Cluster cluster = Cluster.connect(clusterConfig.getHost(), clusterOptions);

            ClusterContainer clusterContainer = this.clusterContainers.get(clusterName);
            clusterConfig.getBuckets().forEach(bucketName -> {
                BucketContainer bucketContainer = clusterContainer.getBucketContainers().get(bucketName);
                bucketContainer.setBucket(cluster.bucket(bucketName));
            });

            clusterContainer.setCluster(cluster);
        });
    }

    public void stop() {
        this.clusterContainers.values().forEach(clusterContainer -> clusterContainer.cluster.disconnect());
        this.clusterContainers.clear();
        this.defaultClusterEnvironment.shutdown();
        LOGGER.info("couchbase shutdown complete");
    }

    public ClusterContainer getClusterContainer(String clusterName) {
        return clusterContainers.get(clusterName);
    }

    public BucketContainer getBucketContainer(String clusterName, String bucketName) {
        return clusterContainers.get(clusterName).getBucketContainers().get(bucketName);
    }

    public static class ClusterContainer {
        private Cluster cluster;
        private Map<String, BucketContainer> bucketContainers;

        public Cluster getCluster() {
            return cluster;
        }

        public void setCluster(Cluster cluster) {
            this.cluster = cluster;
        }

        public Map<String, BucketContainer> getBucketContainers() {
            return bucketContainers;
        }

        public void setBucketContainers(Map<String, BucketContainer> bucketContainers) {
            this.bucketContainers = bucketContainers;
        }
    }

    public static class BucketContainer {
        private Bucket bucket;

        public Bucket getBucket() {
            return bucket;
        }

        public void setBucket(Bucket bucket) {
            this.bucket = bucket;
        }
    }
}