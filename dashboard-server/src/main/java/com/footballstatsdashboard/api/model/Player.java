package com.footballstatsdashboard.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.footballstatsdashboard.api.model.player.Ability;
import com.footballstatsdashboard.api.model.player.Role;
import org.immutables.value.Value;

import com.footballstatsdashboard.api.model.player.Metadata;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutablePlayer.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Value.Style(jdkOnly = true) // Required if the below entity will be used in a Map, List, Set or any other collection
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

    /**
     * Information about the player's role in the team
     */
    @Valid
    List<Role> getRoles();

    /**
     * Information about the player's ability, both current and past data
     */
    @Valid Ability getAbility();
}
