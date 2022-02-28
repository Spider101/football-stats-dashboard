package com.footballstatsdashboard.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class FileUploadConfiguration {
    @NotNull
    @JsonProperty
    private Long maxSizeInBytes;

    @Size(min = 1)
    @NotNull
    @JsonProperty
    private List<String> allowedMediaTypes;

    public Long getMaxSizeInBytes() {
        return maxSizeInBytes;
    }

    public void setMaxSizeInBytes(Long maxSizeInBytes) {
        this.maxSizeInBytes = maxSizeInBytes;
    }

    public List<String> getAllowedMediaTypes() {
        return allowedMediaTypes;
    }

    public void setAllowedMediaTypes(List<String> allowedMediaTypes) {
        this.allowedMediaTypes = allowedMediaTypes;
    }
}