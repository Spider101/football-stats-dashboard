package com.footballstatsdashboard.resources;

import com.footballstatsdashboard.api.model.ImmutablePlayer;
import com.footballstatsdashboard.api.model.Player;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.db.CouchbaseDAO;
import com.footballstatsdashboard.db.key.ResourceKey;
import io.dropwizard.auth.Auth;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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

import static com.footballstatsdashboard.core.utils.Constants.PLAYER_ID;
import static com.footballstatsdashboard.core.utils.Constants.PLAYER_ID_PATH;
import static com.footballstatsdashboard.core.utils.Constants.PLAYER_V1_BASE_PATH;

@Path(PLAYER_V1_BASE_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class PlayerResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerResource.class);

    private final CouchbaseDAO<ResourceKey> couchbaseDAO;
    public PlayerResource(CouchbaseDAO<ResourceKey> couchbaseDAO) {
        this.couchbaseDAO = couchbaseDAO;
    }
    
    @GET
    @Path(PLAYER_ID_PATH)
    public Response getPlayer(
            @Auth @PathParam(PLAYER_ID) UUID playerId) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("getPlayer() request for player with ID: {}", playerId.toString());
        }

        ResourceKey resourceKey = new ResourceKey(playerId);
        Player player = this.couchbaseDAO.getDocument(resourceKey, Player.class).getLeft();
        return Response.ok(player).build();
    }

    @POST
    public Response createPlayer(
            @Auth User user,
            @Valid @NotNull Player incomingPlayer,
            @Context UriInfo uriInfo) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("createPlayer() request.");
        }

        if (incomingPlayer.getRoles().size() == 0 || incomingPlayer.getAttributes().size() == 0) {
            return Response.status(HttpStatus.UNPROCESSABLE_ENTITY_422).entity(incomingPlayer).build();
        }

        // TODO: 17/04/21 add more internal data when business logic becomes complicated
        LocalDate currentDate = LocalDate.now();
        Player newPlayer = ImmutablePlayer.builder()
                .from(incomingPlayer)
                .createdBy(user.getEmail())
                .createdDate(currentDate)
                .lastModifiedDate(currentDate)
                .build();

        ResourceKey resourceKey = new ResourceKey(newPlayer.getId());
        this.couchbaseDAO.insertDocument(resourceKey, newPlayer);

        URI location = uriInfo.getAbsolutePathBuilder().path(newPlayer.getId().toString()).build();
        return Response.created(location).entity(newPlayer).build();
    }

    @PUT
    @Path(PLAYER_ID_PATH)
    public Response updatePlayer(
            @Auth User user,
            @PathParam(PLAYER_ID) UUID playerId,
            @Valid @NotNull Player incomingPlayer) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("updatePlayer() request for player with ID: {}", playerId.toString());
        }

        ResourceKey resourceKey = new ResourceKey(playerId);
        Pair<Player, Long> existingPlayerEntity = this.couchbaseDAO.getDocument(resourceKey, Player.class);

        Player existingPlayer = existingPlayerEntity.getLeft();
        // incoming player's basic details should match with that of the existing player
        if (existingPlayer.getId().equals(incomingPlayer.getId())) {

            // TODO: 15/04/21 add validations by checking incoming player data against existing one
            ImmutablePlayer.Builder updatedPlayerBuilder = ImmutablePlayer.builder()
                    .from(existingPlayer)
                    .metadata(incomingPlayer.getMetadata())
                    .ability(incomingPlayer.getAbility())
                    .roles(incomingPlayer.getRoles())
                    .attributes(incomingPlayer.getAttributes())
                    .lastModifiedDate(LocalDate.now())
                    .createdBy(user.getEmail());

            Player updatedPlayer = updatedPlayerBuilder.build();
            this.couchbaseDAO.updateDocument(resourceKey, updatedPlayer, existingPlayerEntity.getRight());
            return Response.ok(updatedPlayer).build();
        }
        return Response.serverError().entity("Unable to update player with ID: " + incomingPlayer.getId().toString())
                .build();
    }

    @DELETE
    @Path(PLAYER_ID_PATH)
    public Response deletePlayer(
            @Auth @PathParam(PLAYER_ID) UUID playerId) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("deletePlayer() request for player with ID: {}", playerId.toString());
        }

        ResourceKey resourceKey = new ResourceKey(playerId);
        this.couchbaseDAO.deleteDocument(resourceKey);

        return Response.noContent().build();
    }
}
