package com.footballstatsdashboard.resources;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.footballstatsdashboard.api.model.AuthToken;
import com.footballstatsdashboard.api.model.ImmutableAuthToken;
import com.footballstatsdashboard.api.model.ImmutableUser;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.db.AuthTokenDAO;
import com.footballstatsdashboard.db.UserDAO;
import com.footballstatsdashboard.db.key.ResourceKey;
import com.google.common.collect.ImmutableList;
import io.dropwizard.jackson.Jackson;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static com.footballstatsdashboard.core.utils.Constants.HASHING_COST;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for user resource
 */
public class UserResourceTest {
    private static final String URI_PATH = "/users";
    private UserResource userResource;
    private static final ObjectMapper OBJECT_MAPPER = Jackson.newObjectMapper().copy();

    @Mock
    private AuthTokenDAO<ResourceKey> authTokenDAO;

    @Mock
    private UserDAO<ResourceKey> userDAO;

    @Mock
    private UriInfo uriInfo;

    private static final String RAW_PASSWORD = "fake rawPassword";

    @Before
    public void initialize() {
        MockitoAnnotations.openMocks(this);

        UriBuilder uriBuilder = UriBuilder.fromPath(URI_PATH);
        when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);

        userResource = new UserResource(userDAO, authTokenDAO);
    }

    /**
     * given a valid user id, tests that the user entity is successfully fetched from the the couchbase server and
     * returned in the response
     */
    @Test
    public void getUserFetchesUserFromCouchbase() {
        // setup
        UUID userId = UUID.randomUUID();
        User userFromCouchbase = getUserDataStub(userId, false);
        when(userDAO.getDocument(any(), any())).thenReturn(userFromCouchbase);

        // execute
        Response userResponse = userResource.getUser(userId);

        // assert
        verify(userDAO).getDocument(any(), any());
        assertEquals(HttpStatus.OK_200, userResponse.getStatus());
        assertNotNull(userResponse.getEntity());

        User userFromResponse = OBJECT_MAPPER.convertValue(userResponse.getEntity(), User.class);
        assertEquals(userId, userFromResponse.getId());
    }

    /**
     * given a runtime exception is thrown the user DAO when user entity is not found, verifies that the same
     * exception is thrown by the `getUser` resource method as well
     */
    @Test(expected = RuntimeException.class)
    public void getUserWhenUserNotFoundInCouchbase() {
        // setup
        UUID invalidUserId = UUID.randomUUID();
        when(userDAO.getDocument(any(), any()))
                .thenThrow(new RuntimeException("Unable to find document with Id: " + invalidUserId));

        // execute
        userResource.getUser(invalidUserId);

        // assert
        verify(userDAO).getDocument(any(), any());
    }

    /**
     * given a valid user entity in the request, tests that the internal fields are set correctly on the entity and
     * persisted in couchbase
     */
    @Test
    public void createUserPersistsUserInCouchbase() {
        // setup
        User incomingUser = getUserDataStub(null, false);
        ArgumentCaptor<User> newUserCaptor = ArgumentCaptor.forClass(User.class);
        when(userDAO.getUsersByFirstNameLastNameEmail(
                eq(incomingUser.getFirstName()), eq(incomingUser.getLastName()), eq(incomingUser.getEmail())
        )).thenReturn(new ArrayList<>());

        // execute
        Response userResponse = userResource.createUser(incomingUser, uriInfo);

        // assert
        verify(userDAO).getUsersByFirstNameLastNameEmail(anyString(), anyString(), anyString());
        verify(userDAO).insertDocument(any(), newUserCaptor.capture());

        User newUser = newUserCaptor.getValue();
        assertNotNull(newUser.getCreatedBy());
        assertNotNull(newUser.getCreatedDate());
        assertNotNull(newUser.getLastModifiedDate());

        // decrypt user password and match it with the one passed in params
        BCrypt.Result result = BCrypt.verifyer().verify(incomingUser.getPassword().toCharArray(),
                newUser.getPassword());
        assertTrue(result.verified);

        assertEquals(HttpStatus.CREATED_201, userResponse.getStatus());
        assertNotNull(userResponse.getEntity());

        // a userId is set on the player instance created despite not setting one explicitly due to the way the
        // interface has been set up
        assertEquals(URI_PATH + "/" + incomingUser.getId().toString(), userResponse.getLocation().getPath());
    }

    /**
     * given that the user already exists in couchbase, verifies that the incoming user entity is not persisted to
     * couchbase and CONFLICT_409 response is returned
     */
    @Test
    public void createUserWhenUserAlreadyExists() {
        // setup
        User incomingUser = getUserDataStub(null, false);
        when(userDAO.getUsersByFirstNameLastNameEmail(
                eq(incomingUser.getFirstName()), eq(incomingUser.getLastName()), eq(incomingUser.getEmail())
        )).thenReturn(ImmutableList.of(incomingUser));

        // execute
        Response userResponse = userResource.createUser(incomingUser, uriInfo);

        // assert
        verify(userDAO).getUsersByFirstNameLastNameEmail(anyString(), anyString(), anyString());
        verify(userDAO, never()).insertDocument(any(), any());

        assertEquals(HttpStatus.CONFLICT_409, userResponse.getStatus());
        assertNotNull(userResponse.getEntity());
        assertEquals(incomingUser, userResponse.getEntity());
    }

    /**
     * given a valid set of user credentials (email and password) and that no existing auth token exists for that user,
     * verifies that a new auth token is generated for the requesting user
     */
    @Test
    public void authenticateUserCreatesNewAuthToken() {
        // setup
        UUID userId = UUID.randomUUID();
        User userFromCouchbase = getUserDataStub(userId, true);
        User userCredentials = ImmutableUser.builder()
                .email(userFromCouchbase.getEmail())
                .password(RAW_PASSWORD)
                .build();
        when(userDAO.getUserByCredentials(eq(userCredentials.getEmail())))
                .thenReturn(Optional.of(userFromCouchbase));
        when(authTokenDAO.getAuthTokenForUser(eq(userId))).thenReturn(Optional.empty());

        // execute
        Response userResponse = userResource.authenticateUser(userCredentials);

        // assert
        verify(userDAO).getUserByCredentials(anyString());
        verify(authTokenDAO).getAuthTokenForUser(any());
        verify(authTokenDAO, never()).updateLastAccessTime(any(), any());
        verify(authTokenDAO).insertDocument(any(), any());

        assertEquals(HttpStatus.OK_200, userResponse.getStatus());
        assertNotNull(userResponse.getEntity());

        AuthToken authTokenInResponse = OBJECT_MAPPER.convertValue(userResponse.getEntity(), AuthToken.class);
        assertEquals(userId, authTokenInResponse.getUserId());
    }

    /**
     * given that the email in the incoming user credentials do not match for any existing user entity persisted in
     * couchbase, verifies that no auth token is fetched or generated for the user and a BAD_REQUEST_400 response is
     * returned
     */
    @Test
    public void authenticateUserWithInvalidEmail() {
        // setup
        User userCredentials = ImmutableUser.builder()
                .email("invalid email")
                .password("valid password")
                .build();
        when(userDAO.getUserByCredentials(eq(userCredentials.getEmail())))
                .thenReturn(Optional.empty());

        // execute
        Response userResponse = userResource.authenticateUser(userCredentials);

        // assert
        assertInvalidUserCredentials(userCredentials, userResponse);
    }

    /**
     * given that the password in the incoming user credentials is incorrect, verifies that no auth token is fetched
     * or generated for the user and a BAD_REQUEST_400 response is returned
     */
    @Test
    public void authenticateUserWithInvalidPassword() {
        // setup
        UUID userId = UUID.randomUUID();
        User userCredentials = ImmutableUser.builder()
                .email("valid email")
                .password("invalid password")
                .build();
        User userFromCouchbase = getUserDataStub(userId, true);
        when(userDAO.getUserByCredentials(eq(userCredentials.getEmail())))
                .thenReturn(Optional.of(userFromCouchbase));

        // execute
        Response userResponse = userResource.authenticateUser(userCredentials);

        // assert
        assertInvalidUserCredentials(userCredentials, userResponse);
    }

    /**
     * given a valid set of user credentials (email and password) but an existing auth token exists for the user in
     * couchbase, verifies that no new token is generated and instead the existing token is returned with an updated
     * lastAccessUTC field
     */
    @Test
    public void authenticateUserReturnsExistingAuthToken() {
        // setup
        UUID userId = UUID.randomUUID();
        User userFromCouchbase = getUserDataStub(userId, true);
        User userCredentials = ImmutableUser.builder()
                .email(userFromCouchbase.getEmail())
                .password(RAW_PASSWORD)
                .build();
        when(userDAO.getUserByCredentials(eq(userCredentials.getEmail())))
                .thenReturn(Optional.of(userFromCouchbase));

        long currentInstant = Instant.now().toEpochMilli();
        Instant instantInPast = Instant.EPOCH.minus(currentInstant - 1000, ChronoUnit.MILLIS);
        AuthToken existingAuthToken = ImmutableAuthToken.builder()
                .userId(userId)
                .lastAccessUTC(instantInPast)
                .build();
        ArgumentCaptor<AuthToken> existingAuthTokenCaptor = ArgumentCaptor.forClass(AuthToken.class);
        when(authTokenDAO.getAuthTokenForUser(eq(userId)))
                .thenReturn(Optional.of(existingAuthToken));

        // execute
        Response userResponse = userResource.authenticateUser(userCredentials);

        // assert
        verify(userDAO).getUserByCredentials(anyString());
        verify(authTokenDAO).getAuthTokenForUser(any());
        verify(authTokenDAO, never()).insertDocument(any(), any());

        verify(authTokenDAO).updateLastAccessTime(any(), existingAuthTokenCaptor.capture());
        AuthToken capturedExistingAuthToken = existingAuthTokenCaptor.getValue();
        assertEquals(existingAuthToken, capturedExistingAuthToken);

        assertEquals(HttpStatus.OK_200, userResponse.getStatus());
        assertNotNull(userResponse.getEntity());

        AuthToken authTokenInResponse = OBJECT_MAPPER.convertValue(userResponse.getEntity(), AuthToken.class);
        assertEquals(userId, authTokenInResponse.getUserId());
    }

    private User getUserDataStub(UUID userId, boolean isPasswordEncrypted) {
        ImmutableUser.Builder userDataBuilder = ImmutableUser.builder()
                .firstName("fake first name")
                .lastName("fake last name")
                .email("fake email");
        if (userId != null) {
            userDataBuilder.id(userId);
        }

        if (isPasswordEncrypted) {
            userDataBuilder.password(BCrypt.withDefaults().hashToString(HASHING_COST, RAW_PASSWORD.toCharArray()));
        } else {
            userDataBuilder.password(RAW_PASSWORD);
        }

        return userDataBuilder.build();
    }

    private void assertInvalidUserCredentials(User userCredentials, Response userResponse) {
        verify(userDAO).getUserByCredentials(anyString());
        verify(authTokenDAO, never()).getAuthTokenForUser(any());
        verify(authTokenDAO, never()).updateLastAccessTime(any(), any());
        verify(authTokenDAO, never()).insertDocument(any(), any());

        assertEquals(HttpStatus.BAD_REQUEST_400, userResponse.getStatus());
        assertNotNull(userResponse.getEntity());
        assertEquals(userCredentials, userResponse.getEntity());
    }
}