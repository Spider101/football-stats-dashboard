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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableClub.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface Club {

    /**
     * club ID
     */
    @Valid
    @Value.Default
    default UUID getId() { return UUID.randomUUID(); }

    /**
     * club name
     */
    @Valid
    String getName();

    /**
     * club's yearly transfer budget
     */
    @Valid
    BigDecimal getTransferBudget();

    /**
     * club's yearly wage budget
     */
    @Valid
    BigDecimal getWageBudget();

    /**
     * club's income in a year
     */
    @Valid
    BigDecimal getIncome();

    /**
     * club's expenditure in a year
     */
    @Valid
    BigDecimal getExpenditure();

    /**
     * ID of user the club belongs to
     */
    @Valid
    UUID getUserId();

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
    default String getType() { return "Club"; }
}