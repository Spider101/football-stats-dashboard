package com.footballstatsdashboard.db;

import com.footballstatsdashboard.FootballDashboardConfiguration;
import com.footballstatsdashboard.api.model.AuthToken;
import com.footballstatsdashboard.api.model.Club;
import com.footballstatsdashboard.api.model.MatchPerformance;
import com.footballstatsdashboard.api.model.Player;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.api.model.club.BoardObjective;
import com.footballstatsdashboard.api.model.club.ClubSummary;
import com.footballstatsdashboard.api.model.club.ManagerFunds;
import com.footballstatsdashboard.api.model.club.SquadPlayer;
import com.footballstatsdashboard.api.model.matchPerformance.MatchRating;
import com.footballstatsdashboard.api.model.player.Attribute;
import com.footballstatsdashboard.api.model.player.Metadata;
import com.footballstatsdashboard.client.couchbase.CouchbaseClientManager;
import com.footballstatsdashboard.client.couchbase.config.ClusterConfiguration;
import com.footballstatsdashboard.db.couchbase.AuthTokenCouchbaseDAO;
import com.footballstatsdashboard.db.couchbase.ClubCouchbaseDAO;
import com.footballstatsdashboard.db.couchbase.MatchPerformanceCouchbaseDAO;
import com.footballstatsdashboard.db.couchbase.PlayerCouchbaseDAO;
import com.footballstatsdashboard.db.couchbase.UserCouchbaseDAO;
import com.footballstatsdashboard.db.jdbi.AuthTokenJdbiDAO;
import com.footballstatsdashboard.db.jdbi.BoardObjectiveJdbiDAO;
import com.footballstatsdashboard.db.jdbi.ClubJdbiDAO;
import com.footballstatsdashboard.db.jdbi.MatchPerformanceJdbiDAO;
import com.footballstatsdashboard.db.jdbi.PlayerJdbiDAO;
import com.footballstatsdashboard.db.jdbi.UserJdbiDAO;
import com.footballstatsdashboard.db.key.AuthTokenKeyProvider;
import com.footballstatsdashboard.db.key.ClubKeyProvider;
import com.footballstatsdashboard.db.key.MatchPerformanceKeyProvider;
import com.footballstatsdashboard.db.key.PlayerKeyProvider;
import com.footballstatsdashboard.db.key.UserKeyProvider;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Environment;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.immutables.JdbiImmutables;

import java.util.Map;

import static com.footballstatsdashboard.core.utils.Constants.APPLICATION_NAME;

public class DAOFactory {
    private final FootballDashboardConfiguration configuration;
    private final Environment environment;
    private CouchbaseClientManager.ClusterContainer clusterContainer;
    private CouchbaseClientManager.BucketContainer bucketContainer;
    private Jdbi jdbi;

    public DAOFactory(FootballDashboardConfiguration configuration, Environment environment) {
        this.configuration = configuration;
        this.environment = environment;
    }

    public void initialize() {
        if (this.configuration.isShouldStartCouchbaseServer()) {
            // setup couchbase cluster and bucket
            CouchbaseClientManager couchbaseClientManager = new CouchbaseClientManager(APPLICATION_NAME,
                    this.environment, this.configuration.getCouchbaseClientConfiguration());
            this.environment.lifecycle().manage(couchbaseClientManager);

            Map<String, ClusterConfiguration> clusterConfig = this.configuration.getCouchbaseClientConfiguration()
                    .getClusters();
            String clusterName = clusterConfig.entrySet().iterator().next().getKey();
            String bucketName = clusterConfig.get(clusterName).getBuckets().iterator().next();

            this.clusterContainer = couchbaseClientManager.getClusterContainer(clusterName);
            this.bucketContainer = couchbaseClientManager.getBucketContainer(clusterName, bucketName);
        } else {
            JdbiFactory factory = new JdbiFactory();
            this.jdbi = factory.build(this.environment, this.configuration.getDatabase(), "h2");
            jdbi.getConfig(JdbiImmutables.class)
                    .registerImmutable(User.class, AuthToken.class, Club.class, Player.class, MatchPerformance.class,
                            MatchRating.class, ManagerFunds.class, SquadPlayer.class, ClubSummary.class,
                            Metadata.class, Attribute.class, BoardObjective.class);
        }
    }

    public IUserEntityDAO getUserEntityDAO() {
        if (this.configuration.isShouldStartCouchbaseServer()) {
            return new UserCouchbaseDAO(new UserKeyProvider(), () -> this.clusterContainer.getCluster(),
                    () -> this.bucketContainer.getBucket(), this.environment);
        } else {
            return new UserJdbiDAO(this.jdbi);
        }
    }

    public IAuthTokenEntityDAO getAuthTokenEntityDAO() {
        if (this.configuration.isShouldStartCouchbaseServer()) {
            return new AuthTokenCouchbaseDAO(new AuthTokenKeyProvider(), () -> this.clusterContainer.getCluster(),
                    () -> this.bucketContainer.getBucket(), this.environment);
        } else {
            return new AuthTokenJdbiDAO(this.jdbi);
        }
    }

    public IClubEntityDAO getClubEntityDAO() {
        if (this.configuration.isShouldStartCouchbaseServer()) {
            return new ClubCouchbaseDAO(new ClubKeyProvider(), () -> this.clusterContainer.getCluster(),
                    () -> this.bucketContainer.getBucket(), this.environment);
        } else {
            return new ClubJdbiDAO(jdbi);
        }
    }

    public IPlayerEntityDAO getPlayerEntityDAO() {
        if (this.configuration.isShouldStartCouchbaseServer()) {
            return new PlayerCouchbaseDAO(new PlayerKeyProvider(), () -> this.clusterContainer.getCluster(),
                    () -> this.bucketContainer.getBucket(), this.environment);
        } else {
            return new PlayerJdbiDAO(jdbi);
        }
    }

    public IMatchPerformanceEntityDAO getMatchPerformanceEntityDAO() {
        if (this.configuration.isShouldStartCouchbaseServer()) {
            return new MatchPerformanceCouchbaseDAO(new MatchPerformanceKeyProvider(),
                    () -> this.clusterContainer.getCluster(), () -> this.bucketContainer.getBucket(), this.environment);
        } else {
            return new MatchPerformanceJdbiDAO(jdbi);
        }
    }

    public IBoardObjectiveEntityDAO getBoardObjectiveEntityDAO() {
        if (this.configuration.isShouldStartCouchbaseServer()) {
            // TODO: 16/04/22 initialize the couchbase implementation for the board objective entity DAO when ready
            return null;
        } else {
            return new BoardObjectiveJdbiDAO(jdbi);
        }
    }
}