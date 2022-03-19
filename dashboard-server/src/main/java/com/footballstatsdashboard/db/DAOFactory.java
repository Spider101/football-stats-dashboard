package com.footballstatsdashboard.db;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.footballstatsdashboard.FootballDashboardConfiguration;
import com.footballstatsdashboard.api.model.Player;
import com.footballstatsdashboard.client.couchbase.CouchbaseClientManager;
import com.footballstatsdashboard.client.couchbase.config.ClusterConfiguration;
import com.footballstatsdashboard.db.couchbase.AuthTokenCouchbaseDAO;
import com.footballstatsdashboard.db.couchbase.ClubCouchbaseDAO;
import com.footballstatsdashboard.db.couchbase.MatchPerformanceCouchbaseDAO;
import com.footballstatsdashboard.db.couchbase.PlayerCouchbaseDAO;
import com.footballstatsdashboard.db.couchbase.UserCouchbaseDAO;
import com.footballstatsdashboard.db.key.AuthTokenKeyProvider;
import com.footballstatsdashboard.db.key.ClubKeyProvider;
import com.footballstatsdashboard.db.key.MatchPerformanceKeyProvider;
import com.footballstatsdashboard.db.key.PlayerKeyProvider;
import com.footballstatsdashboard.db.key.UserKeyProvider;
import io.dropwizard.setup.Environment;

import java.util.Map;

import static com.footballstatsdashboard.core.utils.Constants.APPLICATION_NAME;

public class DAOFactory {
    private final FootballDashboardConfiguration configuration;
    private final Environment environment;

    private Cluster couchbaseCluster;
    private Bucket couchbaseBucket;

    public DAOFactory(FootballDashboardConfiguration configuration, Environment environment) {
        this.configuration = configuration;
        this.environment = environment;
    }

    public void initialize() {
        if (configuration.isShouldStartCouchbaseServer()) {
            // setup couchbase cluster and bucket
            CouchbaseClientManager couchbaseClientManager = new CouchbaseClientManager(APPLICATION_NAME, environment,
                    configuration.getCouchbaseClientConfiguration());
            environment.lifecycle().manage(couchbaseClientManager);

            Map<String, ClusterConfiguration> clusterConfig = configuration.getCouchbaseClientConfiguration()
                    .getClusters();
            String clusterName = clusterConfig.entrySet().iterator().next().getKey();
            String bucketName = clusterConfig.get(clusterName).getBuckets().iterator().next();

            this.couchbaseCluster = couchbaseClientManager.getClusterContainer(clusterName).getCluster();
            this.couchbaseBucket = couchbaseClientManager.getBucketContainer(clusterName, bucketName).getBucket();
        }
    }

    public IUserEntityDAO getUserEntityDAO() {
        return new UserCouchbaseDAO(new UserKeyProvider(), this.couchbaseCluster, this.couchbaseBucket,
                this.couchbaseBucket.name());
    }

    public IAuthTokenEntityDAO getAuthTokenEntityDAO() {
        return new AuthTokenCouchbaseDAO(new AuthTokenKeyProvider(), this.couchbaseCluster, this.couchbaseBucket,
                this.couchbaseBucket.name());
    }

    public IClubEntityDAO getClubEntityDAO() {
        return new ClubCouchbaseDAO(new ClubKeyProvider(), this.couchbaseCluster, this.couchbaseBucket,
                this.couchbaseBucket.name());
    }

    public IEntityDAO<Player> getPlayerEntityDAO() {
        return new PlayerCouchbaseDAO(new PlayerKeyProvider(), this.couchbaseBucket);
    }

    public IMatchPerformanceEntityDAO getMatchPerformanceEntityDAO() {
        return new MatchPerformanceCouchbaseDAO(new MatchPerformanceKeyProvider(), this.couchbaseCluster,
                this.couchbaseBucket, this.couchbaseBucket.name());
    }
}