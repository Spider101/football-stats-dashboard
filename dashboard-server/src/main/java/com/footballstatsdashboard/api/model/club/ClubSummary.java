package com.footballstatsdashboard.api.model.club;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.immutables.value.Value;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;

@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableClubSummary.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ClubSummary {
    /**
     * club ID
     */
    @Valid UUID getClubId();

    /**
     * club name
     */
    @Valid
    String getName();

    /**
     * club logo file key
     */
    @Valid
    String getLogo();

    // TODO: 1/6/2022 update all local date times to zoned date times to involve timezones as well
    /**
     *  timestamp when club data was created
     */
    @Valid
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    LocalDate getCreatedDate();
}