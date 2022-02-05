package com.footballstatsdashboard.api.model.player;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AttributeCategory {
    TECHNICAL("Technical"),
    PHYSICAL("Physical"),
    MENTAL("Mental");

    private final String description;
    AttributeCategory(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return this.description;
    }

    @JsonCreator
    public static AttributeCategory fromValue(String value) {
        return AttributeCategory.valueOf(value.toUpperCase());
    }
}