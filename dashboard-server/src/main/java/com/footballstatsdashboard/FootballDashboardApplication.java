package com.footballstatsdashboard;

import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.client.couchbase.CouchbaseClientManager;
import com.footballstatsdashboard.client.couchbase.config.ClusterConfiguration;
import com.footballstatsdashboard.core.exceptions.ServiceExceptionMapper;
import com.footballstatsdashboard.core.service.auth.CustomAuthenticator;
import com.footballstatsdashboard.core.service.auth.CustomAuthorizer;
import com.footballstatsdashboard.core.utils.DashboardInternalModule;
import com.footballstatsdashboard.core.utils.DashboardReadonlyModule;
import com.footballstatsdashboard.db.AuthTokenDAO;
import com.footballstatsdashboard.db.ClubDAO;
import com.footballstatsdashboard.db.CouchbaseDAO;
import com.footballstatsdashboard.db.MatchPerformanceDAO;
import com.footballstatsdashboard.db.UserDAO;
import com.footballstatsdashboard.db.key.AuthTokenKeyProvider;
import com.footballstatsdashboard.db.key.ClubKeyProvider;
import com.footballstatsdashboard.db.key.MatchPerformanceKeyProvider;
import com.footballstatsdashboard.db.key.PlayerKeyProvider;
import com.footballstatsdashboard.db.key.ResourceKey;
import com.footballstatsdashboard.db.key.UserKeyProvider;
import com.footballstatsdashboard.health.FootballDashboardHealthCheck;
import com.footballstatsdashboard.resources.ClubResource;
import com.footballstatsdashboard.resources.CountryFlagsLookupResource;
import com.footballstatsdashboard.resources.FileUploadResource;
import com.footballstatsdashboard.resources.MatchPerformanceResource;
import com.footballstatsdashboard.resources.PlayerResource;
import com.footballstatsdashboard.resources.UserResource;
import com.footballstatsdashboard.services.ClubService;
import com.footballstatsdashboard.services.CountryFlagsLookupService;
import com.footballstatsdashboard.services.FileUploadService;
import com.footballstatsdashboard.services.PlayerService;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
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
        bootstrap.addBundle(new MultiPartBundle());
    }

    @Override
    public void run(final FootballDashboardConfiguration configuration, final Environment environment) {
        // initialize serialization modules
        environment.getObjectMapper().registerModule(new DashboardInternalModule());
        environment.getObjectMapper().registerModule(new DashboardReadonlyModule());

        // setup couchbase cluster and bucket
        CouchbaseClientManager couchbaseClientManager = new CouchbaseClientManager(getName(), environment,
                configuration.getCouchbaseClientConfiguration());

        // skip starting the couchbase server if need be
        if (configuration.isShouldStartCouchbaseServer()) {
            environment.lifecycle().manage(couchbaseClientManager);
        }

        Map<String, ClusterConfiguration> clusterConfig = configuration.getCouchbaseClientConfiguration().getClusters();
        String clusterName = clusterConfig.entrySet().iterator().next().getKey();
        String bucketName = clusterConfig.get(clusterName).getBuckets().iterator().next();

        CouchbaseDAO<ResourceKey> playerCouchbaseDAO = new CouchbaseDAO<>(
                couchbaseClientManager.getBucketContainer(clusterName, bucketName),
                new PlayerKeyProvider()
        );

        ClubDAO<ResourceKey> clubCouchbaseDAO = new ClubDAO<>(
                couchbaseClientManager.getBucketContainer(clusterName, bucketName),
                couchbaseClientManager.getClusterContainer(clusterName),
                new ClubKeyProvider()
        );

        MatchPerformanceDAO<ResourceKey> matchPerformanceDAO = new MatchPerformanceDAO<>(
                couchbaseClientManager.getBucketContainer(clusterName, bucketName),
                couchbaseClientManager.getClusterContainer(clusterName),
                new MatchPerformanceKeyProvider()
        );

        UserDAO<ResourceKey> userCouchbaseDAO = new UserDAO<>(
                couchbaseClientManager.getBucketContainer(clusterName, bucketName),
                couchbaseClientManager.getClusterContainer(clusterName),
                new UserKeyProvider()
        );

        AuthTokenDAO<ResourceKey> authTokenDAO = new AuthTokenDAO<>(
                couchbaseClientManager.getBucketContainer(clusterName, bucketName),
                couchbaseClientManager.getClusterContainer(clusterName),
                new AuthTokenKeyProvider(), environment.getObjectMapper());

        // setup services
        ClubService clubService = new ClubService(clubCouchbaseDAO);
        PlayerService playerService = new PlayerService(playerCouchbaseDAO);
        CountryFlagsLookupService countryFlagsLookupService = new CountryFlagsLookupService();
        FileUploadService fileUploadService = new FileUploadService(configuration.getFileUploadConfiguration());

        // setup resources
        environment.jersey().register(new UserResource(userCouchbaseDAO, authTokenDAO));
        environment.jersey().register(new PlayerResource(playerService, clubService));
        environment.jersey().register(new ClubResource(clubService));
        environment.jersey().register(new MatchPerformanceResource(matchPerformanceDAO));
        environment.jersey().register(new CountryFlagsLookupResource(countryFlagsLookupService));
        environment.jersey().register(new FileUploadResource(fileUploadService));

        // Register OAuth authentication
        environment.jersey()
                .register(new AuthDynamicFeature(new OAuthCredentialAuthFilter.Builder<User>()
                        .setAuthenticator(new CustomAuthenticator(authTokenDAO, userCouchbaseDAO))
                        .setAuthorizer(new CustomAuthorizer())
                        .setPrefix("BEARER")
                        .buildAuthFilter()
                ));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));

        // setup health checks
        environment.healthChecks().register(this.getName(), new FootballDashboardHealthCheck());

        // setup exception mappers
        environment.jersey().register(new ServiceExceptionMapper());
        // this ensures json errors are shown in detail
        environment.jersey().register(new JsonProcessingExceptionMapper(true));

        LOGGER.info("All resources added for {}", getName());
    }

}