package com.footballstatsdashboard.client.couchbase.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class ClusterConfiguration {
    /**
     * The connection string for connecting to the cluster
     */
    @NotNull
    @JsonProperty
    private String host;

    /**
     * The username to be used for connecting to the cluster
     */
    @NotNull
    @JsonProperty
    private String username;

    /**
     * The password to be used for connecting to the cluster
     */
    @NotNull
    @JsonProperty
    private String password;

    /**
     * list of the names of the buckets in the cluster
     */
    @NotNull
    @Size(min = 1)
    @JsonProperty
    private List<String> buckets;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<String> buckets) {
        this.buckets = buckets;
    }
}
