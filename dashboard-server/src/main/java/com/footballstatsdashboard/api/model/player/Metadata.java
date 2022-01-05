package com.footballstatsdashboard.api.model.player;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Represents the metadata for a player
 */
@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableMetadata.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface Metadata {

    int MIN_PLAYER_AGE = 15;
    int MAX_PLAYER_AGE = 50;

    /**
     * player's full name
     */
    @Valid
    String getName();

    /**
     * player's current club name
     */
    @Valid
    @Nullable
    String getClub();

    /**
     * player's nationality
     */
    @Valid
    String getCountry();

    /**
     * url for player's photograph
     */
    @Valid
    @Nullable // TODO: remove nullable when client functionality is implemented for uploading player image
    String getPhoto();

    /**
     * url for player's club logo
     */
    @Valid
    @Nullable
    String getClubLogo();

    /**
     * url for logo of player's club
     */
    @Valid
    @Nullable
    String getCountryLogo();

    /**
     * player's current age in years
     */
    @Valid
    @Min(value = MIN_PLAYER_AGE, message = "Age should not be less than 15")
    @Max(value = MAX_PLAYER_AGE, message = "Age should not be greater than 50")
    Integer getAge();
}