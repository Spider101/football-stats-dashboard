package com.footballstatsdashboard.api.model.player;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.footballstatsdashboard.core.utils.Readonly;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Represents the attributes of a player
 */
@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableAttribute.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Value.Style(jdkOnly = true) // Required if the below entity will be used in a Map, List, Set or any other collection
public interface Attribute {

    /**
     * name of the attribute
     */
    @Valid
    String getName();

    /**
     * category the attribute belongs to
     */
    @Valid
    @Nullable
    @Readonly
    AttributeCategory getCategory();

    /**
     * group the attribute belongs to
     */
    @Valid
    @Nullable
    @Readonly
    AttributeGroup getGroup();

    /**
     * current value of the attribute
     */
    @Valid
    Integer getValue();

    /**
     * list of historical values of the attribute corresponding to each past month
     */
    @Valid
    @Nullable
    @Size(min = 1)
    List<Integer> getHistory();
}