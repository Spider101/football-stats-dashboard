package com.footballstatsdashboard.api.model.player;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Period;

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
    @Nullable
    String getName();

    /**
     * player's date of birth
     */
    @Valid
    @Nullable
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    LocalDate getDateOfBirth();

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
    @Nullable
    String getCountry();

    /**
     * url for player's photograph
     */
    @Valid
    @Nullable
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
    @Value.Derived
    default Integer getAge() {
        if (getDateOfBirth() != null) {
            return Period.between(getDateOfBirth(), LocalDate.now()).getYears();
        }
        return null;
    }
}