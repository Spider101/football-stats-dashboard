package com.footballstatsdashboard.core.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.util.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class FixtureLoader {
    private static final Logger LOG = LoggerFactory.getLogger(FixtureLoader.class);
    private final ObjectMapper objectMapper;

    public FixtureLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T loadFixture(String filePath, Class<T> clazz) throws IOException, IllegalArgumentException {
        return this.objectMapper.readValue(getFixtureAsString(filePath), clazz);
    }

    public <T> T loadFixture(String filePath, TypeReference<T> typeRef) throws IOException, IllegalArgumentException {
        return this.objectMapper.readValue(getFixtureAsString(filePath), typeRef);
    }

    private String getFixtureAsString(String fileName) {
        URL resource = Resources.getResource(fileName);
        try {
            return Resources.toString(resource, StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            LOG.error("Could not load fixture from {}", fileName);
            throw new IllegalArgumentException(ioException);
        }
    }
}