package com.footballstatsdashboard.api.model.club;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.footballstatsdashboard.core.utils.InternalField;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import javax.validation.Valid;
import java.util.UUID;

@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableBoardObjective.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface BoardObjective {

    /**
     * board objective id
     */
    @Valid
    @Value.Default
    default UUID getId() {
        return UUID.randomUUID();
    }

    /**
     * objective title
     */
    @Valid
    String getTitle();

    /**
     * objective description
     */
    @Valid
    String getDescription();

    /**
     * objective completion status
     */
    @Valid
    boolean isCompleted();

    /**
     * ID of the club the objective belongs to
     */
    @Valid
    UUID getClubId();

    /**
     * represent the type of entity
     */
    @Nullable
    @InternalField
    @Value.Default
    default String getType() {
        return "BoardObjective";
    }
}