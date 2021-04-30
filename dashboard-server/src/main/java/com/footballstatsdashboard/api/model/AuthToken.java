package com.footballstatsdashboard.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableAuthToken.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface AuthToken {

    /**
     * ID for the auth token
     */
    @Valid
    @Value.Default
    default UUID getId() { return UUID.randomUUID(); }

    /**
     * user ID for user associated with auth token
     */
    @Valid
    @NotNull
    UUID getUserId();

    /**
     * last access timestamp for auth token
     */
    @Valid
    @NotNull
    OffsetDateTime getLastAccessUTC();
}
