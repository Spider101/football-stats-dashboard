package com.footballstatsdashboard.api.model.club;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.footballstatsdashboard.core.utils.InternalField;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import javax.validation.Valid;
import java.time.LocalDate;
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
    @Nullable
    @InternalField
    UUID getClubId();

    // TODO: 12/05/21 update all local date times to zoned date times to involve timezones as well
    /**
     * timestamp when club data was created
     */
    @Valid
    @Nullable
    @InternalField
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    LocalDate getCreatedDate();

    /**
     * timestamp when club data was last modified
     */
    @Valid
    @Nullable
    @InternalField
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    LocalDate getLastModifiedDate();

    /**
     * represents entity that requested club data be created
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
        return "BoardObjective";
    }
}