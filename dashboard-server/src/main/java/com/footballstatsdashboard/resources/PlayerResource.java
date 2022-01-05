package com.footballstatsdashboard.resources;

import com.footballstatsdashboard.api.model.Player;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.api.model.club.Club;
import com.footballstatsdashboard.db.CouchbaseDAO;
import com.footballstatsdashboard.db.key.ResourceKey;
import com.footballstatsdashboard.services.PlayerService;
import io.dropwizard.auth.Auth;
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
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import static com.footballstatsdashboard.core.utils.Constants.PLAYER_ID;
import static com.footballstatsdashboard.core.utils.Constants.PLAYER_ID_PATH;
import static com.footballstatsdashboard.core.utils.Constants.PLAYER_V1_BASE_PATH;

@Path(PLAYER_V1_BASE_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class PlayerResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerResource.class);

    private final PlayerService playerService;
    private final CouchbaseDAO<ResourceKey> clubCouchbaseDAO;

    public PlayerResource(PlayerService playerService, CouchbaseDAO<ResourceKey> clubCouchbaseDAO) {
        this.playerService = playerService;
        this.clubCouchbaseDAO = clubCouchbaseDAO;
    }

    @GET
    @Path(PLAYER_ID_PATH)
    public Response getPlayer(
            @Auth @PathParam(PLAYER_ID) UUID playerId) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("getPlayer() request for player with ID: {}", playerId.toString());
        }

        Player player = this.playerService.getPlayer(playerId);
        return Response.ok(player).build();
    }

    @POST
    public Response createPlayer(
            @Auth User user,
            @Valid @NotNull Player incomingPlayer,
            @Context UriInfo uriInfo) throws IOException {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("createPlayer() request.");
        }

        if (incomingPlayer.getRoles().size() == 0 || incomingPlayer.getAttributes().size() == 0) {
            LOGGER.warn("Player entity in request has empty roles or attributes list!");
            return Response.status(HttpStatus.UNPROCESSABLE_ENTITY_422).entity(incomingPlayer).build();
        }

        // fetch details of club the incoming player belongs to
        // TODO: 1/5/2022 fetch club using club service when it is ready, instead of directly using DAO
        ResourceKey resourceKeyForClub = new ResourceKey(incomingPlayer.getClubId());
        Club clubDataForNewPlayer = this.clubCouchbaseDAO.getDocument(resourceKeyForClub, Club.class);

        Player newPlayer = this.playerService.createPlayer(incomingPlayer, clubDataForNewPlayer, user.getEmail());

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

        Player existingPlayer = this.playerService.getPlayer(playerId);
        // incoming player's basic details should match with that of the existing player
        if (existingPlayer.getId().equals(incomingPlayer.getId())) {
            // TODO: 15/04/21 add validations by checking incoming player data against existing one
            Player updatedPlayer = this.playerService.updatePlayer(incomingPlayer, existingPlayer, playerId);
            return Response.ok(updatedPlayer).build();
        }
        return Response.serverError()
                .entity("Unable to update player with ID: " + incomingPlayer.getId().toString())
                .build();
    }

    @DELETE
    @Path(PLAYER_ID_PATH)
    public Response deletePlayer(
            @Auth @PathParam(PLAYER_ID) UUID playerId) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("deletePlayer() request for player with ID: {}", playerId);
        }

        this.playerService.deletePlayer(playerId);
        return Response.noContent().build();
    }
}