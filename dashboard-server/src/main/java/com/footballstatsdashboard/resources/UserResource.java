package com.footballstatsdashboard.resources;

import com.footballstatsdashboard.api.model.ImmutableUser;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.db.CouchbaseDAO;
import com.footballstatsdashboard.db.key.ResourceKey;
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
import java.time.LocalDate;
import java.util.UUID;

import static com.footballstatsdashboard.core.utils.Constants.USER_ID;
import static com.footballstatsdashboard.core.utils.Constants.USER_ID_PATH;
import static com.footballstatsdashboard.core.utils.Constants.USER_V1_BASE_PATH;

@Path(USER_V1_BASE_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

    public CouchbaseDAO<ResourceKey> couchbaseDAO;
    public UserResource(CouchbaseDAO<ResourceKey> couchbaseDAO) {
        this.couchbaseDAO = couchbaseDAO;
    }

    @GET
    @Path(USER_ID_PATH)
    public Response getUser(
            @PathParam(USER_ID) UUID userId) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("getUser() request for user with ID: {}", userId.toString());
        }

        ResourceKey resourceKey = new ResourceKey(userId);
        User user = this.couchbaseDAO.getDocument(resourceKey, User.class).getLeft();
        return Response.ok(user).build();
    }

    @POST
    public Response createUser(
            @Valid @NotNull User incomingUserDetails,
            @Context UriInfo uriInfo) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("createUser() request.");
        }

        // TODO: 28/04/21 add validation to see if user with given email address already exists or not
        LocalDate currentDate = LocalDate.now();
        User newUser = ImmutableUser.builder()
                .from(incomingUserDetails)
                .createdDate(currentDate)
                .lastModifiedDate(currentDate)
                .build();

        ResourceKey resourceKey = new ResourceKey(newUser.getId());
        this.couchbaseDAO.insertDocument(resourceKey, newUser);

        URI location = uriInfo.getAbsolutePathBuilder().path(newUser.getId().toString()).build();
        return Response.created(location).entity(newUser).build();
    }
}
