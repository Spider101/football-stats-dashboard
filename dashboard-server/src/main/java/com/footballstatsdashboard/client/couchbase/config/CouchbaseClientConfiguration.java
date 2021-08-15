package com.footballstatsdashboard.client.couchbase.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

public class CouchbaseClientConfiguration {
    private static final long DEFAULT_KV_TIMEOUT_MS = 10000L;

    @JsonProperty
    private long kvTimeout = DEFAULT_KV_TIMEOUT_MS;

    @NotNull
    @Size(min = 1)
    @Valid
    @JsonProperty
    private Map<String, ClusterConfiguration> clusters;

    public long getKvTimeout() {
        return kvTimeout;
    }

    public void setKvTimeout(long kvTimeout) {
        this.kvTimeout = kvTimeout;
    }

    public Map<String, ClusterConfiguration> getClusters() {
        return clusters;
    }

    public void setClusters(Map<String, ClusterConfiguration> clusters) {
        this.clusters = clusters;
    }
}