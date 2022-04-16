package com.footballstatsdashboard.resources;

import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.api.model.club.BoardObjective;
import com.footballstatsdashboard.core.exceptions.ServiceException;
import com.footballstatsdashboard.services.BoardObjectiveService;
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

import static com.footballstatsdashboard.core.utils.Constants.BOARD_OBJECTIVE_ID;
import static com.footballstatsdashboard.core.utils.Constants.BOARD_OBJECTIVE_ID_PATH;
import static com.footballstatsdashboard.core.utils.Constants.BOARD_OBJECTIVE_V1_BASE_PATH;
import static com.footballstatsdashboard.core.utils.Constants.CLUB_ID;

@Path(BOARD_OBJECTIVE_V1_BASE_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class BoardObjectiveResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(BoardObjectiveResource.class);

    private final BoardObjectiveService boardObjectiveService;
    private final ClubService clubService;
    public BoardObjectiveResource(BoardObjectiveService boardObjectiveService, ClubService clubService) {
        this.boardObjectiveService = boardObjectiveService;
        this.clubService = clubService;
    }

    @GET
    @Path(BOARD_OBJECTIVE_ID_PATH)
    public Response getBoardObjective(
            @Auth User user,
            @PathParam(CLUB_ID) UUID clubId,
            @PathParam(BOARD_OBJECTIVE_ID) UUID boardObjectiveId) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("getBoardObjective() request for board objective with ID: {}", boardObjectiveId);
        }

        // TODO: 13/04/22 move the entity existence check to DAO layer so we don't have to fetch the entire entity just
        //  to check that it exists. Also add a belongs to check after splitting it since it is encapsulated inside the
        //  getClub call at the moment
        this.clubService.getClub(clubId, user.getId());

        BoardObjective boardObjective = boardObjectiveService.getBoardObjective(boardObjectiveId, clubId);
        return Response.ok(boardObjective).build();
    }

    @POST
    public Response createBoardObjective(
            @Auth User user,
            @PathParam(CLUB_ID) UUID clubId,
            @Valid @NotNull BoardObjective incomingBoardObjective,
            @Context UriInfo uriInfo) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("createBoardObjective() request.");
        }

        // TODO: 13/04/22 move the entity existence check to DAO layer so we don't have to fetch the entire entity just
        //  to check that it exists. Also add a belongs to check after splitting it since it is encapsulated inside the
        //  getClub call at the moment
        this.clubService.getClub(clubId, user.getId());

        BoardObjective newBoardObjective = boardObjectiveService.createBoardObjective(incomingBoardObjective, clubId,
                user.getEmail());

        URI location = uriInfo.getAbsolutePathBuilder().path(newBoardObjective.getId().toString()).build();
        return Response.created(location).entity(newBoardObjective).build();
    }

    @PUT
    @Path(BOARD_OBJECTIVE_ID_PATH)
    public Response updateBoardObjective(
            @Auth User user,
            @PathParam(CLUB_ID) UUID clubId,
            @PathParam(BOARD_OBJECTIVE_ID) UUID boardObjectiveId,
            @Valid @NotNull BoardObjective incomingBoardObjective) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("updateBoardObjective() request for board objective with ID: {}", boardObjectiveId);
        }

        // TODO: 13/04/22 move the entity existence check to DAO layer so we don't have to fetch the entire entity just
        //  to check that it exists. Also add a belongs to check after splitting it since it is encapsulated inside the
        //  getClub call at the moment
        this.clubService.getClub(clubId, user.getId());
        BoardObjective updatedBoardObjective = boardObjectiveService.updateBoardObjective(boardObjectiveId, clubId,
                incomingBoardObjective);
        return Response.ok().entity(updatedBoardObjective).build();
    }

    @DELETE
    @Path(BOARD_OBJECTIVE_ID_PATH)
    public Response deleteBoardObjective(
            @Auth User user,
            @PathParam(CLUB_ID) UUID clubId,
            @PathParam(BOARD_OBJECTIVE_ID) UUID boardObjectiveId) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("deleteBoardObjective() request for board objective with ID: {}", boardObjectiveId);
        }

        if (!this.clubService.doesClubBelongToUser(clubId, user.getId())) {
            LOGGER.error("Board Objective with ID: {} does not belong to user making request", boardObjectiveId);
            throw new ServiceException(HttpStatus.FORBIDDEN_403, "User does not have access to this board objective!");
        }

        this.boardObjectiveService.deleteBoardObjective(boardObjectiveId, clubId);
        return Response.noContent().build();
    }

    @GET
    @Path("/all")
    public Response getAllBoardObjectives(
            @Auth User user,
            @PathParam(CLUB_ID) UUID clubId) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("getAllBoardObjectives()  request for club with ID : {}", clubId);
        }

        if (!this.clubService.doesClubBelongToUser(clubId, user.getId())) {
            LOGGER.error("Club with ID: {} does not belong to user making request", clubId);
            throw new ServiceException(HttpStatus.FORBIDDEN_403, "User does not have access to this club!");
        }

        List<BoardObjective> boardObjectives = this.boardObjectiveService.getAllBoardObjectivesForClub(clubId);
        return Response.ok().entity(boardObjectives).build();
    }
}