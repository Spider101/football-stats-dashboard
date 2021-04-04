package com.footballstatsdashboard;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.*;

import javax.validation.Valid;
import javax.validation.constraints.*;

public class FootballDashboardConfiguration extends Configuration {

    @Valid
    @NotNull
    @JsonProperty
    private Long appId;
}
