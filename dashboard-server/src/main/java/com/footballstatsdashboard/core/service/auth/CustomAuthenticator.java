package com.footballstatsdashboard.core.service.auth;

import com.footballstatsdashboard.api.model.AuthToken;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.db.AuthTokenDAO;
import com.footballstatsdashboard.db.UserDAO;
import com.footballstatsdashboard.db.key.ResourceKey;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class CustomAuthenticator implements Authenticator<String, User> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticator.class);
    private static final int AUTH_TOKEN_EXPIRE_TIME_SECONDS = 3600;
    private final AuthTokenDAO<ResourceKey> authTokenDAO;
    private final UserDAO<ResourceKey> userDAO;

    public CustomAuthenticator(AuthTokenDAO<ResourceKey> authTokenDAO, UserDAO<ResourceKey> userDAO) {
        this.authTokenDAO = authTokenDAO;
        this.userDAO = userDAO;
    }

    @Override
    public Optional<User> authenticate(String authTokenId) throws AuthenticationException {

        // validate authTokenId string as UUID
        UUID validAuthToken;
        try {
            validAuthToken = UUID.fromString(authTokenId);
        } catch (IllegalArgumentException illegalArgumentException) {
            LOGGER.error(authTokenId + " is not a valid UUID");
            return Optional.empty();
        }

        // retrieve the auth token entity using the authTokenId
        ResourceKey authTokenKey = new ResourceKey(validAuthToken);
        AuthToken authToken = this.authTokenDAO.getDocument(authTokenKey, AuthToken.class);

        if (authToken != null) {
            // check if access token has expired
            Duration duration = Duration.between(authToken.getLastAccessUTC(), Instant.now());
            if (duration.getSeconds() <=  AUTH_TOKEN_EXPIRE_TIME_SECONDS) {
                // touch access token to push expiration
                this.authTokenDAO.updateLastAccessTime(authTokenKey, authToken);

                // return the user entity as the authenticated principal
                ResourceKey userKey = new ResourceKey(authToken.getUserId());
                User user = this.userDAO.getDocument(userKey, User.class);
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}