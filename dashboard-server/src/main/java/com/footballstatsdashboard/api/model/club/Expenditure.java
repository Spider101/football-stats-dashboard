package com.footballstatsdashboard.api.model.club;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableExpenditure.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Value.Style(jdkOnly = true) // Required if the below entity will be used in a Map, List, Set or any other collection
public interface Expenditure {
    /**
     * current value of expenditure
     */
    @Valid BigDecimal getCurrent();

    /**
     * list of all values of expenditure entity in the past, including the current value
     */
    @Valid
    @Size(min = 1)
    List<BigDecimal> getHistory();
}