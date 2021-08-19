package com.footballstatsdashboard.api.model.club;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.validation.Valid;
import java.util.UUID;

@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableSquadPlayer.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface SquadPlayer {

    /**
     * Player's name
     */
    @Valid
    String getName();

    /**
     * Player's nationality
     */
    @Valid
    String getCountry();

    /**
     * Player's role
     */
    @Valid
    String getRole();

    /**
     * Player's current ability
     */
    @Valid
    Integer getCurrentAbility();

    /**
     * Player's ID
     */
    @Valid
    UUID getPlayerId();
}