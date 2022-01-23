package com.footballstatsdashboard.resources;

import com.footballstatsdashboard.api.model.Club;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.api.model.club.ClubSummary;
import com.footballstatsdashboard.api.model.club.SquadPlayer;
import com.footballstatsdashboard.core.exceptions.ServiceException;
import com.footballstatsdashboard.services.ClubService;
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
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static com.footballstatsdashboard.core.utils.Constants.CLUB_ID;
import static com.footballstatsdashboard.core.utils.Constants.CLUB_ID_PATH;
import static com.footballstatsdashboard.core.utils.Constants.CLUB_V1_BASE_PATH;

@Path(CLUB_V1_BASE_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class ClubResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClubResource.class);

    private final ClubService clubService;

    public ClubResource(ClubService clubService) {
        this.clubService = clubService;
    }

    @GET
    @Path(CLUB_ID_PATH)
    public Response getClub(
            @Auth User user,
            @PathParam(CLUB_ID) UUID clubId) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("getClub() request for club with ID: {}", clubId);
        }

        Club club = this.clubService.getClub(clubId, user.getId());
        return Response.ok().entity(club).build();
    }

    @POST
    public Response createClub(
            @Auth User user,
            @Valid @NotNull Club incomingClub,
            @Context UriInfo uriInfo) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("createClub() request.");
        }

        Club newClub = this.clubService.createClub(incomingClub, user.getId(), user.getEmail());

        URI location = uriInfo.getAbsolutePathBuilder().path(newClub.getId().toString()).build();
        return Response.created(location).entity(newClub).build();
    }

    @PUT
    @Path(CLUB_ID_PATH)
    public Response updateClub(
            @Auth User user,
            @PathParam(CLUB_ID) UUID existingClubId,
            @Valid @NotNull Club incomingClub) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("updateClub() request for club with ID: {}", existingClubId);
        }

        Club existingClub = this.clubService.getClub(existingClubId, user.getId());
        if (!existingClub.getId().equals(incomingClub.getId())) {
            String errorMessage = String.format(
                    "Incoming club entity ID: %s does not match ID of existing club entity %s.",
                    incomingClub.getId(), existingClub.getId()
            );
            LOGGER.error(errorMessage);
            throw new ServiceException(HttpStatus.CONFLICT_409, errorMessage);
        }

        Club updatedClub = this.clubService.updateClub(incomingClub, existingClub, existingClubId);
        return Response.ok().entity(updatedClub).build();
    }

    @DELETE
    @Path(CLUB_ID_PATH)
    public Response deleteClub(
            @Auth User user,
            @PathParam(CLUB_ID) UUID clubId) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("deleteClub() request for club with ID: {}", clubId);
        }

        this.clubService.deleteClub(clubId, user.getId());
        return Response.noContent().build();
    }

    @GET
    @Path("/all")
    public Response getClubsByUserId(@Auth User user) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("getClubsByUserId() request for user");
        }

        List<ClubSummary> clubsByUserId = this.clubService.getClubSummariesByUserId(user.getId());
        return Response.ok().entity(clubsByUserId).build();
    }

    @GET
    @Path(CLUB_ID_PATH + "/squadPlayers")
    public Response getSquadPlayers(
            @Auth @PathParam(CLUB_ID) UUID clubId) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("getSquadPlayers() request for club with ID: {}", clubId);
        }

        List<SquadPlayer> squadPlayerList = this.clubService.getSquadPlayers(clubId);
        return Response.ok().entity(squadPlayerList).build();
    }
}