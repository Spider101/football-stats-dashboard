package com.footballstatsdashboard.api.model.player;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AttributeGroup {
    ATTACKING("Attacking"),
    DEFENDING("Defending"),
    AERIAL("Aerial"),
    SPEED("Speed"),
    VISION("Vision");

    private final String description;
    AttributeGroup(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return this.description;
    }

    @JsonCreator
    public static AttributeGroup fromValue(String value) {
        return AttributeGroup.valueOf(value.toUpperCase());
    }
}