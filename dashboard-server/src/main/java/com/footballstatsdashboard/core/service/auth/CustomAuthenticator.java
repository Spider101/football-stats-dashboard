package com.footballstatsdashboard.core.service.auth;

import com.footballstatsdashboard.api.model.AuthToken;
import com.footballstatsdashboard.api.model.ImmutableAuthToken;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.db.IAuthTokenEntityDAO;
import com.footballstatsdashboard.db.IUserEntityDAO;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class CustomAuthenticator implements Authenticator<String, User> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticator.class);
    private static final int AUTH_TOKEN_EXPIRE_TIME_SECONDS = 3600;
    private final IAuthTokenEntityDAO authTokenDAO;
    private final IUserEntityDAO userDAO;

    public CustomAuthenticator(IAuthTokenEntityDAO authTokenDAO, IUserEntityDAO userDAO) {
        this.authTokenDAO = authTokenDAO;
        this.userDAO = userDAO;
    }

    @Override
    // TODO: 19/03/22 check if this exception needs to be thrown
    public Optional<User> authenticate(String authTokenId) throws AuthenticationException {

        // validate authTokenId string as UUID
        UUID validAuthTokenId;
        try {
            validAuthTokenId = UUID.fromString(authTokenId);
        } catch (IllegalArgumentException illegalArgumentException) {
            LOGGER.error(authTokenId + " is not a valid UUID");
            return Optional.empty();
        }

        // retrieve the auth token entity using the authTokenId
        AuthToken existingAuthToken = this.authTokenDAO.getEntity(validAuthTokenId);

        if (existingAuthToken != null) {
            // check if access token has expired
            Duration duration = Duration.between(existingAuthToken.getLastAccessUTC(), Instant.now());
            if (duration.getSeconds() <=  AUTH_TOKEN_EXPIRE_TIME_SECONDS) {
                // touch access token to push expiration
                updateLastAccessTimeOnAuthToken(existingAuthToken);

                // return the user entity as the authenticated principal
                User user = this.userDAO.getEntity(existingAuthToken.getUserId());
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    private void updateLastAccessTimeOnAuthToken(AuthToken existingAuthToken) {
        AuthToken updatedAuthToken = ImmutableAuthToken.builder()
                .from(existingAuthToken)
                .lastAccessUTC(Instant.now())
                .build();
        this.authTokenDAO.updateEntity(existingAuthToken.getId(), updatedAuthToken);
    }
}