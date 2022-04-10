package com.footballstatsdashboard.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.footballstatsdashboard.api.model.matchPerformance.MatchRating;
import com.footballstatsdashboard.core.utils.InternalField;
import org.immutables.value.Value;
import org.jdbi.v3.core.mapper.Nested;

import javax.annotation.Nullable;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;

@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableMatchPerformance.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface MatchPerformance {

    /**
     * match performance ID
     */
    @Valid
    @Value.Default
    default UUID getId() {
        return UUID.randomUUID();
    }

    /**
     *  player ID
     */
    @Valid
    UUID getPlayerId();

    /**
     * competition ID
     */
    @Valid
    UUID getCompetitionId();

    /**
     * Number of appearances made in the competition by the player
     */
    @Valid
    Integer getAppearances();

    /**
     * Number of goals scored in the competition by the player
     */
    @Valid
    Integer getGoals();

    /**
     * Number of penalties scored in the competition by the player
     */
    @Valid
    Integer getPenalties();

    /**
     * Number of assists made in the competition by the player
     */
    @Valid
    Integer getAssists();

    /**
     * Number of Player of the Match awards won in the competition by the player
     */
    @Valid
    Integer getPlayerOfTheMatch();

    /**
     * Number of yellow cards accumulated in the competition by the player
     */
    @Valid
    Integer getYellowCards();

    /**
     * Number of red cards accumulated in the competition by the player
     */
    @Valid
    Integer getRedCards();

    /**
     * Number of tackles made in the competition by the player
     */
    @Valid
    Integer getTackles();

    /**
     * Number of fouls committed in the competition by the player
     */
    @Valid
    Integer getFouls();

    /**
     * Number of dribbles completed in the competition by the player
     */
    @Valid
    Integer getDribbles();

    /**
     * Player's pass completion rate in the competition
     */
    @Valid
    Float getPassCompletionRate();

    /**
     * Player's match rating in the competition
     */
    @Valid
    @Nested("matchRating")
    MatchRating getMatchRating();

    // TODO: 12/05/21 update all local date times to zoned date times to involve timezones as well
    /**
     * timestamp when player data was created
     */
    @Valid
    @Nullable
    @InternalField
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    LocalDate getCreatedDate();

    /**
     * timestamp when player data was last modified
     */
    @Valid
    @Nullable
    @InternalField
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    LocalDate getLastModifiedDate();

    /**
     * represents entity that requested player data be created
     */
    @Nullable
    @InternalField
    String getCreatedBy();

    /**
     * represent the type of entity
     */
    @Nullable
    @InternalField
    @Value.Default
    default String getType() {
        return "MatchPerformance";
    }
}