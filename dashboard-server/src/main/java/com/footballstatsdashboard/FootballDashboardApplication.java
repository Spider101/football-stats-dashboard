package com.footballstatsdashboard;

import com.footballstatsdashboard.client.couchbase.CouchbaseClientManager;
import com.footballstatsdashboard.client.couchbase.config.ClusterConfiguration;
import com.footballstatsdashboard.core.utils.PlayerInternalModule;
import com.footballstatsdashboard.db.CouchbaseDAO;
import com.footballstatsdashboard.db.UserDAO;
import com.footballstatsdashboard.db.key.PlayerKeyProvider;
import com.footballstatsdashboard.db.key.ResourceKey;
import com.footballstatsdashboard.db.key.UserKeyProvider;
import com.footballstatsdashboard.health.FootballDashboardHealthCheck;
import com.footballstatsdashboard.resources.PlayerResource;
import com.footballstatsdashboard.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.footballstatsdashboard.core.utils.Constants.APPLICATION_NAME;

public class FootballDashboardApplication extends Application<FootballDashboardConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FootballDashboardApplication.class);

    public static void main(final String[] args) throws Exception {
        new FootballDashboardApplication().run(args);
    }

    @Override
    public String getName() {
        return APPLICATION_NAME;
    }

    @Override
    public void initialize(final Bootstrap<FootballDashboardConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final FootballDashboardConfiguration configuration, final Environment environment) {

        environment.getObjectMapper().registerModule(new PlayerInternalModule());

        // setup couchbase cluster and bucket
        CouchbaseClientManager couchbaseClientManager = new CouchbaseClientManager(getName(), environment,
                configuration.getCouchbaseClientConfiguration());
        environment.lifecycle().manage(couchbaseClientManager);

        Map<String, ClusterConfiguration> clusterConfig = configuration.getCouchbaseClientConfiguration().getClusters();
        String clusterName = clusterConfig.entrySet().iterator().next().getKey();
        String bucketName = clusterConfig.get(clusterName).getBuckets().iterator().next();

        CouchbaseDAO<ResourceKey> playerCouchbaseDAO = new CouchbaseDAO<>(
                couchbaseClientManager.getBucketContainer(clusterName, bucketName),
                new PlayerKeyProvider()
        );

        UserDAO<ResourceKey> userCouchbaseDAO = new UserDAO<>(
                couchbaseClientManager.getBucketContainer(clusterName, bucketName),
                couchbaseClientManager.getClusterContainer(clusterName),
                new UserKeyProvider()
        );

        // setup resources
        environment.jersey().register(new UserResource(userCouchbaseDAO));
        environment.jersey().register(new PlayerResource(playerCouchbaseDAO));

        // setup health checks
        environment.healthChecks().register(this.getName(), new FootballDashboardHealthCheck());

        // add exception mapper so that json errors are show in detail
        environment.jersey().register(new JsonProcessingExceptionMapper(true));

        LOGGER.info("All resources added for {}", getName());
    }

}
