package com.footballstatsdashboard.api.model.player;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AttributeCategory {
    TECHNICAL("Technical"),
    PHYSICAL("Physical"),
    MENTAL("Mental");

    private final String value;
    AttributeCategory(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }
}