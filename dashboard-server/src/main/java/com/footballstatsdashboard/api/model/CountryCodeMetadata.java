package com.footballstatsdashboard.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.validation.Valid;

@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableCountryCodeMetadata.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface CountryCodeMetadata {

    /**
     * country name
     */
    @Valid
    String getCountryName();

    /**
     * country name
     */
    @Valid
    String getCountryCode();
}