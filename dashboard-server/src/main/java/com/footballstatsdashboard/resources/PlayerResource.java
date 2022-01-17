package com.footballstatsdashboard.resources;

import com.footballstatsdashboard.api.model.Player;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.api.model.Club;
import com.footballstatsdashboard.core.exceptions.ServiceException;
import com.footballstatsdashboard.services.ClubService;
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
    private final ClubService clubService;

    public PlayerResource(PlayerService playerService, ClubService clubService) {
        this.playerService = playerService;
        this.clubService = clubService;
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

        // fetch details of club the incoming player belongs to
        Club clubDataForNewPlayer = this.clubService.getClub(incomingPlayer.getClubId());

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
        if (!existingPlayer.getId().equals(incomingPlayer.getId())) {
            String errorMessage = String.format(
                    "Incoming player ID: %s does not match ID of existing player entity in couchbase: %s.",
                    incomingPlayer.getId(), existingPlayer.getId()
            );
            LOGGER.error(errorMessage);
            throw new ServiceException(HttpStatus.CONFLICT_409, errorMessage);
        }

        Player updatedPlayer = this.playerService.updatePlayer(incomingPlayer, existingPlayer, playerId);
        return Response.ok(updatedPlayer).build();
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