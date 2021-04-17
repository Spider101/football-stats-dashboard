package com.footballstatsdashboard.api.model.player;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

/**
 *  Represents the role played by a player in the team
 */
@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableRole.class)
@Value.Style(jdkOnly = true) // Required if the below entity will be used in a Map, List, Set or any other collection
public interface Role {

    /**
     * name of role played by player in team
     */
    @Valid
    String getName();

    /**
     * list of player attributes associated with role
     */
    @Valid
    @Size(min = 1)
    List<String> getAssociatedAttributes();
}