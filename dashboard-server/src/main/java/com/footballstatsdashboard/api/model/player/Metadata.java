package com.footballstatsdashboard.api.model.player;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import javax.validation.Valid;

/**
 * Represents the metadata for a player
 */
@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableMetadata.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface Metadata {

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
    Integer getAge();
}