package com.footballstatsdashboard.health;

import com.codahale.metrics.health.HealthCheck;

public class FootballDashboardHealthCheck extends HealthCheck {

    /**
     * Create an instance of this health check
     */
    public FootballDashboardHealthCheck() {
        super();
    }
    
    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}