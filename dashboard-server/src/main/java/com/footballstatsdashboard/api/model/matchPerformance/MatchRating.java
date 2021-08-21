package com.footballstatsdashboard.api.model.matchPerformance;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableMatchRating.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Value.Style(jdkOnly = true) // Required if the below entity will be used in a Map, List, Set or any other collection
public interface MatchRating {

    @Valid
    Float getCurrent();

    @Valid
    @Size(min = 1)
    List<Float> getHistory();
}