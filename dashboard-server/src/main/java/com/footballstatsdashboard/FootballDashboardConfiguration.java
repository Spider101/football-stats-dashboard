package com.footballstatsdashboard;

import com.footballstatsdashboard.client.couchbase.config.CouchbaseClientConfiguration;
import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class FootballDashboardConfiguration extends Configuration {

    @Valid
    @NotNull
    @JsonProperty
    private Long appId;

    @Valid
    @NotNull
    @JsonProperty
    private CouchbaseClientConfiguration couchbase;

    @Valid
    @NotNull
    @JsonProperty
    private boolean shouldStartCouchbaseServer;

    public CouchbaseClientConfiguration getCouchbaseClientConfiguration() {
        return couchbase;
    }

    public boolean isShouldStartCouchbaseServer() {
        return shouldStartCouchbaseServer;
    }
}