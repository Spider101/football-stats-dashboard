package com.footballstatsdashboard.api.model.club;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.footballstatsdashboard.core.utils.Readonly;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableManagerFunds.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Value.Style(jdkOnly = true) // Required if the below entity will be used in a Map, List, Set or any other collection
public interface ManagerFunds {
    /**
     * current value of the funds allocated to the manager
     */
    @Valid BigDecimal getCurrent();

    /**
     * list of all values of the manager funds entity in the past, including the current value
     */
    @Valid
    @Nullable
    @Readonly
    @Size(min = 1)
    List<BigDecimal> getHistory();
}