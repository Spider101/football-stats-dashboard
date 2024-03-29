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

        BoardObjective boardObjective = boardObjectiveService.getBoardObjective(boardObjectiveId, clubId, user.getId());
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

        // match the club ID in the incoming request against the clubId in the path param
        if (!clubId.equals(incomingBoardObjective.getClubId())) {
            String errorMessage = String.format(
                    "The club ID (%s) in the incoming request does not match the club ID in the request url (%s)",
                    incomingBoardObjective.getClubId(), clubId
            );
            LOGGER.error(errorMessage);
            throw new ServiceException(HttpStatus.CONFLICT_409,
                    "Club ID on incoming data does not match club ID in existing data");
        }

        // ensure user has access to the club for which the board objective is to be created
        if (!this.clubService.doesClubBelongToUser(clubId, user.getId())) {
            LOGGER.error("Club with ID: {} does not belong to user making request (ID: {})",
                    clubId, user.getId());
            throw new ServiceException(HttpStatus.FORBIDDEN_403, "User does not have access to this club!");
        }

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

        /*
        verify that board objective id in the incoming request matches with the id in the existing data. The board
        objective ID in the path param can be considered a proxy for the corresponding entity stored in the database
        (assuming it exists)
        also match the club ID in the incoming request against the clubId in the path param
         */
        if (!boardObjectiveId.equals(incomingBoardObjective.getId())
                || !clubId.equals(incomingBoardObjective.getClubId())) {
            String errorMessage = String.format(
                    "Incoming board objective ID: %s does not match ID of existing board objective: %s or the club ID" +
                            " (%s) in the incoming request does not match the club ID in the request url (%s)",
                    incomingBoardObjective.getId(), boardObjectiveId, incomingBoardObjective.getClubId(), clubId
            );
            LOGGER.error(errorMessage);
            throw new ServiceException(HttpStatus.CONFLICT_409, "Board objective ID or club ID on incoming data does" +
                    " not match board objective ID or club ID in existing data");
        }

        BoardObjective updatedBoardObjective = boardObjectiveService.updateBoardObjective(boardObjectiveId, clubId,
                user.getId(), incomingBoardObjective);
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

        this.boardObjectiveService.deleteBoardObjective(boardObjectiveId, clubId, user.getId());
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