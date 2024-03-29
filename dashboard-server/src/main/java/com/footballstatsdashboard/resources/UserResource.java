package com.footballstatsdashboard.resources;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.footballstatsdashboard.api.model.AuthToken;
import com.footballstatsdashboard.api.model.ImmutableAuthToken;
import com.footballstatsdashboard.api.model.ImmutableUser;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.db.IAuthTokenEntityDAO;
import com.footballstatsdashboard.db.IUserEntityDAO;
import io.dropwizard.auth.Auth;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.footballstatsdashboard.core.utils.Constants.HASHING_COST;
import static com.footballstatsdashboard.core.utils.Constants.USER_ID;
import static com.footballstatsdashboard.core.utils.Constants.USER_ID_PATH;
import static com.footballstatsdashboard.core.utils.Constants.USER_V1_BASE_PATH;

@Path(USER_V1_BASE_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

    private final IUserEntityDAO userDAO;
    private final IAuthTokenEntityDAO authTokenDAO;

    public UserResource(IUserEntityDAO userDAO,
                        IAuthTokenEntityDAO authTokenDAO) {
        this.userDAO = userDAO;
        this.authTokenDAO = authTokenDAO;
    }

    @GET
    @Path(USER_ID_PATH)
    public Response getUser(
            @Auth @PathParam(USER_ID) UUID userId) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("getUser() request for user with ID: {}", userId.toString());
        }

        User user = this.userDAO.getEntity(userId);
        return Response.ok(user).build();
    }

    @POST
    public Response createUser(
            @Valid @NotNull User incomingUserDetails,
            @Context UriInfo uriInfo) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("createUser() request.");
        }

        // check if there are any existing users with the same first name, last name, email combination
        List<User> existingUsers = this.userDAO.getExistingUsers(incomingUserDetails.getFirstName(),
                incomingUserDetails.getLastName(), incomingUserDetails.getEmail());
        if (existingUsers.size() > 0) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("There is an existing user with the same first name, last name and email combination!");
            }
            return Response.status(HttpStatus.CONFLICT_409).entity(incomingUserDetails).build();
        }

        // encrypt the password before persisting the user
        String encryptedPassword = BCrypt.withDefaults()
                .hashToString(HASHING_COST, incomingUserDetails.getPassword().toCharArray());
        LocalDate currentDate = LocalDate.now();

        User newUser = ImmutableUser.builder()
                .from(incomingUserDetails)
                .password(encryptedPassword)
                .createdBy(incomingUserDetails.getEmail())
                .createdDate(currentDate)
                .lastModifiedDate(currentDate)
                .build();

        this.userDAO.insertEntity(newUser);

        URI location = uriInfo.getAbsolutePathBuilder().path(newUser.getId().toString()).build();
        return Response.created(location).entity(newUser).build();
    }

    @POST
    @Path("/authenticate")
    public Response authenticateUser(
            @Valid @NotNull User userCredentials) {
        Optional<User> user = this.userDAO.getUserByEmailAddress(userCredentials.getEmail());

        if (user.isEmpty()) {
            LOGGER.error("Invalid email provided: " + userCredentials.getEmail());
            return Response.status(HttpStatus.BAD_REQUEST_400).entity(userCredentials).build();
        }

        // user found, verify encrypted password matches password passed in credentials
        User validUser = user.get();
        BCrypt.Result result = BCrypt.verifyer().verify(userCredentials.getPassword().toCharArray(),
                validUser.getPassword());
        if (!result.verified) {
            LOGGER.error("Password provided in credentials does not match encrypted password hash");
            return Response.status(HttpStatus.BAD_REQUEST_400).entity(userCredentials).build();
        }

        // password is verified, check if an auth token exists for the user, touch the lastAccessDate property
        // and then return it
        Optional<AuthToken> authToken = this.authTokenDAO.getAuthTokenForUser(validUser.getId());

        if (authToken != null && authToken.isPresent()) {
            AuthToken validAuthToken = authToken.get();

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Existing Auth Token: " + validAuthToken.getId() + " found for userId: "
                        + validUser.getId());
            }

            updateLastAccessTimeOnAuthToken(authToken.get());

            return Response.ok().entity(validAuthToken).build();
        }

        // existing auth token not found, so generate one and return it
        AuthToken freshAuthToken = ImmutableAuthToken.builder()
                .userId(validUser.getId())
                .lastAccessUTC(Instant.now())
                .build();
        this.authTokenDAO.insertEntity(freshAuthToken);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fresh Auth Token: " + freshAuthToken.getId() + " generated");
        }
        return Response.ok().entity(freshAuthToken).build();
    }

    private void updateLastAccessTimeOnAuthToken(AuthToken existingAuthToken) {
        AuthToken updatedAuthToken = ImmutableAuthToken.builder()
                .from(existingAuthToken)
                .lastAccessUTC(Instant.now())
                .build();
        this.authTokenDAO.updateEntity(existingAuthToken.getId(), updatedAuthToken);
    }
}