package com.footballstatsdashboard;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class FootballDashboardApplication extends Application<FootballDashboardConfiguration> {

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
        // TODO: implement application
    }

}
