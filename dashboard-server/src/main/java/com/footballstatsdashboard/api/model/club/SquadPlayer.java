package com.footballstatsdashboard.api.model.club;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.validation.Valid;
import java.util.List;
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
     * Image url for flag representing player's nationality
     */
    @Valid
    String getCountryFlag();

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
     * Player's form in recent matches
     */
    @Valid
    List<Float> getRecentForm();

    /**
     * Player's ID
     */
    @Valid
    UUID getPlayerId();
}