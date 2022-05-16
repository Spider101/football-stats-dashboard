package com.footballstatsdashboard;

import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.core.exceptions.ServiceExceptionMapper;
import com.footballstatsdashboard.core.service.auth.CustomAuthenticator;
import com.footballstatsdashboard.core.service.auth.CustomAuthorizer;
import com.footballstatsdashboard.core.utils.DashboardInternalModule;
import com.footballstatsdashboard.core.utils.DashboardReadonlyModule;
import com.footballstatsdashboard.db.DAOFactory;
import com.footballstatsdashboard.db.IUserEntityDAO;
import com.footballstatsdashboard.health.FootballDashboardHealthCheck;
import com.footballstatsdashboard.resources.BoardObjectiveResource;
import com.footballstatsdashboard.resources.ClubResource;
import com.footballstatsdashboard.resources.CountryFlagsLookupResource;
import com.footballstatsdashboard.resources.FileStorageResource;
import com.footballstatsdashboard.resources.MatchPerformanceResource;
import com.footballstatsdashboard.resources.PlayerResource;
import com.footballstatsdashboard.resources.UserResource;
import com.footballstatsdashboard.services.BoardObjectiveService;
import com.footballstatsdashboard.services.ClubService;
import com.footballstatsdashboard.services.CountryFlagsLookupService;
import com.footballstatsdashboard.services.FileStorageService;
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

        // setup data access layer
        DAOFactory daoFactory = new DAOFactory(configuration, environment);
        daoFactory.initialize();
        // since this entity is referenced by other entities, need to initialize it before others
        IUserEntityDAO userEntityDAO = daoFactory.getUserEntityDAO();

        // setup services
        ClubService clubService = new ClubService(daoFactory.getClubEntityDAO());
        PlayerService playerService = new PlayerService(daoFactory.getPlayerEntityDAO());
        CountryFlagsLookupService countryFlagsLookupService = new CountryFlagsLookupService();
        FileStorageService fileUploadService = new FileStorageService(configuration.getFileUploadConfiguration());
        BoardObjectiveService boardObjectiveService =
                new BoardObjectiveService(daoFactory.getBoardObjectiveEntityDAO());

        // setup resources
        environment.jersey().register(new UserResource(userEntityDAO, daoFactory.getAuthTokenEntityDAO()));
        environment.jersey().register(new PlayerResource(playerService, clubService));
        environment.jersey().register(new ClubResource(clubService, fileUploadService));
        environment.jersey().register(new MatchPerformanceResource(daoFactory.getMatchPerformanceEntityDAO()));
        environment.jersey().register(new CountryFlagsLookupResource(countryFlagsLookupService));
        environment.jersey().register(new FileStorageResource(fileUploadService));
        environment.jersey().register(new BoardObjectiveResource(boardObjectiveService, clubService));

        // Register OAuth authentication
        CustomAuthenticator customAuthenticator = new CustomAuthenticator(daoFactory.getAuthTokenEntityDAO(),
                userEntityDAO);
        environment.jersey()
                .register(new AuthDynamicFeature(new OAuthCredentialAuthFilter.Builder<User>()
                        .setAuthenticator(customAuthenticator)
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