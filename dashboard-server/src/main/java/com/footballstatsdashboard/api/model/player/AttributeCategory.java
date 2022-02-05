package com.footballstatsdashboard.api.model.player;

import com.fasterxml.jackson.annotation.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum AttributeCategory {
    TECHNICAL("Technical"),
    PHYSICAL("Physical"),
    MENTAL("Mental");

    private final String value;
    private static final Logger LOGGER = LoggerFactory.getLogger(AttributeCategory.class);
    AttributeCategory(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }
}