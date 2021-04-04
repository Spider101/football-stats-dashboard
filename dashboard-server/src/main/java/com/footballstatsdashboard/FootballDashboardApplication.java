package com.footballstatsdashboard;

import com.footballstatsdashboard.health.FootballDashboardHealthCheck;
import com.footballstatsdashboard.resources.PlayerResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FootballDashboardApplication extends Application<FootballDashboardConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FootballDashboardApplication.class);

    public static void main(final String[] args) throws Exception {
        new FootballDashboardApplication().run(args);
    }

    @Override
    public String getName() {
        return "FootballDashboard";
    }

    @Override
    public void initialize(final Bootstrap<FootballDashboardConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final FootballDashboardConfiguration configuration,
                    final Environment environment) {

        // setup resources
        environment.jersey().register(new PlayerResource());

        // setup health checks
        environment.healthChecks().register(this.getName(), new FootballDashboardHealthCheck());

        LOGGER.info("All resources added for {}", getName());
    }

}
