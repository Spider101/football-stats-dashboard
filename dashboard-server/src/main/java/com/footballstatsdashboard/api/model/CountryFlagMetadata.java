package com.footballstatsdashboard.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.validation.Valid;
import java.util.UUID;

@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableCountryFlagMetadata.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface CountryFlagMetadata extends CountryCodeMetadata {
    /**
     * country flag ID
     */
    @Valid
    @Value.Default
    default UUID getId() {
        return UUID.randomUUID();
    }

    /**
     *
     */
    @Valid
    String getCountryFlagUrl();
}