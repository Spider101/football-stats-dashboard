package com.footballstatsdashboard.core.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.footballstatsdashboard.core.validations.Validation;
import io.dropwizard.jackson.Jackson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceException extends RuntimeException {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceException.class);
    private static final ObjectMapper OBJECT_MAPPER = Jackson.newObjectMapper().copy();

    private final String responseBody;
    private final int responseStatus;
    private List<Validation> validationList;

    public ServiceException(int responseStatus, String message) {
        super(message);
        this.responseStatus = responseStatus;
        this.responseBody = getCustomResponseBody();
    }

    public ServiceException(int responseStatus, String message, List<Validation> validationList) {
        super(message);
        this.responseStatus = responseStatus;
        this.validationList = validationList;
        this.responseBody = getCustomResponseBody();
    }

    public String getResponseBody() {
        return this.responseBody;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public List<Validation> getValidationList() {
        return validationList;
    }

    private String getCustomResponseBody() {
        if (StringUtils.isBlank(this.getMessage())) {
            return null;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("status", this.getResponseStatus());
        params.put("message", this.getMessage());

        if (this.getValidationList() != null) {
            params.put("validations", this.getValidationList());
        }

        try {
            return OBJECT_MAPPER.writeValueAsString(params);
        } catch (JsonProcessingException ex) {
            LOGGER.warn("Unable to serialize params map to string!", ex);
            return null;
        }
    }
}