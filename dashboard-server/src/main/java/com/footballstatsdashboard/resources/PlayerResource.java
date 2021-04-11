package com.footballstatsdashboard.resources;

import com.footballstatsdashboard.api.model.ImmutablePlayer;
import com.footballstatsdashboard.api.model.Player;
import com.footballstatsdashboard.api.model.player.ImmutableMetadata;
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
import java.time.Month;
import java.util.UUID;

@Path("football-stats-dashboard/v1/players")
@Produces(MediaType.APPLICATION_JSON)
public class PlayerResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerResource.class);

    public CouchbaseDAO<ResourceKey> couchbaseDAO;
    public PlayerResource(CouchbaseDAO<ResourceKey> couchbaseDAO) {
        this.couchbaseDAO = couchbaseDAO;
    }
    
    @GET
    @Path("/{playerId}")
    public Response getPlayer(
            @PathParam("playerId") UUID playerId) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("getPlayer() request for player with ID: {}", playerId.toString());
        }
        Player dummyPlayer = ImmutablePlayer.builder()
                .metadata(
                        ImmutableMetadata.builder()
                                .name("Sander Gard Bolin Berge")
                                .club("Sheffield United")
                                .country("Norway")
                                .photo("http://lorempixel.com/640/480/people?random=7")
                                .clubLogo("http://lorempixel.com/640/480/abstract?random=11")
                                .countryLogo("https://s3.amazonaws.com/uifaces/faces/twitter/j04ntoh/128.jpg?random=1")
                                .dateOfBirth(LocalDate.of(1998, Month.FEBRUARY, 18))
                                .build()
                )
                .build();
        return Response.ok(dummyPlayer).build();
    }

    @POST
    public Response createPlayer(
            @Valid @NotNull Player incomingPlayer,
            @Context UriInfo uriInfo) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("createPlayer() request.");
        }

        ResourceKey resourceKey = new ResourceKey(incomingPlayer.getId());
        this.couchbaseDAO.insertDocument(resourceKey, incomingPlayer);

        // TODO: 11/04/21 build a new player entity from the incoming entity when the business logic becomes more
        //  complex and use that in the response instead of the deserialized entity directly
        URI location = uriInfo.getAbsolutePathBuilder().path(incomingPlayer.getId().toString()).build();
        return Response.created(location).entity(incomingPlayer).build();
    }
}
