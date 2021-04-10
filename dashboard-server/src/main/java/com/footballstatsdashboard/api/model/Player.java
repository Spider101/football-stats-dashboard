package com.footballstatsdashboard.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import com.footballstatsdashboard.api.model.player.Metadata;

import javax.validation.Valid;
import java.util.UUID;

@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutablePlayer.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface Player {

    /**
     * player ID
     */
    @Valid
    @Value.Default
    default UUID getId() { return UUID.randomUUID(); }

    /**
     * Information about the player
     */
    @Valid
    Metadata getMetadata();
}
