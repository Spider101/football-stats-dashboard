package com.footballstatsdashboard.api.model.player;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AttributeGroup {
    ATTACKING("Attacking"),
    DEFENDING("Defending"),
    AERIAL("Aerial"),
    SPEED("Speed"),
    VISION("Vision");

    private final String value;
    AttributeGroup(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }
}