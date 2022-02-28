package com.footballstatsdashboard;

import com.footballstatsdashboard.client.couchbase.config.CouchbaseClientConfiguration;
import com.footballstatsdashboard.config.FileUploadConfiguration;
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

    @Valid
    @NotNull
    @JsonProperty
    private FileUploadConfiguration fileUpload;

    public CouchbaseClientConfiguration getCouchbaseClientConfiguration() {
        return couchbase;
    }

    public boolean isShouldStartCouchbaseServer() {
        return shouldStartCouchbaseServer;
    }

    public FileUploadConfiguration getFileUploadConfiguration() {
        return fileUpload;
    }
}