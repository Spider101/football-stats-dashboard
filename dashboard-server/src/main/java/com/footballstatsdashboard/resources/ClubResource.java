package com.footballstatsdashboard.resources;

import com.footballstatsdashboard.api.model.club.Club;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.api.model.club.ImmutableClub;
import com.footballstatsdashboard.api.model.club.SquadPlayer;
import com.footballstatsdashboard.db.ClubDAO;
import com.footballstatsdashboard.db.key.ResourceKey;
import com.google.common.collect.ImmutableMap;
import io.dropwizard.auth.Auth;
import org.apache.commons.lang3.StringUtils;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.footballstatsdashboard.core.utils.Constants.CLUB_ID;
import static com.footballstatsdashboard.core.utils.Constants.CLUB_ID_PATH;
import static com.footballstatsdashboard.core.utils.Constants.CLUB_V1_BASE_PATH;

@Path(CLUB_V1_BASE_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class ClubResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClubResource.class);

    private final ClubDAO<ResourceKey> clubDAO;

    public ClubResource(ClubDAO<ResourceKey> clubDAO) {
        this.clubDAO = clubDAO;
    }

    @GET
    @Path(CLUB_ID_PATH)
    public Response getClub(
            @Auth @PathParam(CLUB_ID) UUID clubId) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("getClub() request for club with ID: {}", clubId);
        }

        ResourceKey resourceKey = new ResourceKey(clubId);
        Club club = this.clubDAO.getDocument(resourceKey, Club.class);
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

        // TODO: 17/04/21 add more internal data when business logic becomes complicated
        if (StringUtils.isEmpty(incomingClub.getName())) {
            String errorMessage = "Empty club name is not allowed!";
            int statusCode = HttpStatus.BAD_REQUEST_400;
            LOGGER.error(errorMessage);
            Map<String, Object> params = ImmutableMap.of(
                    "status", statusCode,
                    "message", errorMessage
            );
            return Response.status(statusCode).entity(params).build();
        }

        LocalDate currentDate = LocalDate.now();
        Club newClub = ImmutableClub.builder()
                .from(incomingClub)
                .userId(user.getId())
                .createdDate(currentDate)
                .lastModifiedDate(currentDate)
                .createdBy(user.getEmail())
                .build();

        ResourceKey resourceKey = new ResourceKey(newClub.getId());
        this.clubDAO.insertDocument(resourceKey, newClub);

        URI location = uriInfo.getAbsolutePathBuilder().path(newClub.getId().toString()).build();
        return Response.created(location).entity(newClub).build();
    }

    @PUT
    @Path(CLUB_ID_PATH)
    public Response updateClub(
            @Auth @PathParam(CLUB_ID) UUID existingClubId,
            @Valid @NotNull Club incomingClub) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("updateClub() request for club with ID: {}", existingClubId);
        }

        ResourceKey resourceKey = new ResourceKey(existingClubId);
        Club existingClub = this.clubDAO.getDocument(resourceKey, Club.class);

        if (existingClub.getId().equals(incomingClub.getId())) {

            // TODO: 15/04/21 add validations by checking incoming club data against existing one
            Club updatedClub = ImmutableClub.builder()
                    .from(existingClub)
                    .name(incomingClub.getName())
                    .income(incomingClub.getIncome())
                    .expenditure(incomingClub.getExpenditure())
                    .transferBudget(incomingClub.getTransferBudget())
                    .wageBudget(incomingClub.getWageBudget())
                    .lastModifiedDate(LocalDate.now())
                    .build();

            this.clubDAO.updateDocument(resourceKey, updatedClub);
            return Response.ok().entity(updatedClub).build();
        }

        LOGGER.error("Incoming club entity ID: {} does not match ID of existing club entity in couchbase: {}. " +
                "Aborting operation!", incomingClub.getId(), existingClub.getId());
        return Response.serverError()
                .entity("Unable to update club with ID: " + incomingClub.getId().toString())
                .build();
    }

    @DELETE
    @Path(CLUB_ID_PATH)
    public Response deleteClub(
            @Auth @PathParam(CLUB_ID) UUID clubId) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("deleteClub() request for club with ID: {}", clubId);
        }

        ResourceKey resourceKey = new ResourceKey(clubId);
        this.clubDAO.deleteDocument(resourceKey);

        return Response.noContent().build();
    }

    @GET
    @Path("/all")
    public Response getClubsByUserId(@Auth User user) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("getClubsByUserId() request for user");
        }
        List<Club> clubsByUserId = clubDAO.getClubsByUserId(user.getId());
        return Response.ok().entity(clubsByUserId).build();
    }

    @GET
    @Path(CLUB_ID_PATH + "/squadPlayers")
    public Response getSquadPlayers(
            @Auth @PathParam(CLUB_ID) UUID clubId) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("getSquadPlayers() request for club with ID: {}", clubId);
        }
        List<SquadPlayer> squadPlayerList = clubDAO.getPlayersInClub(clubId);
        return Response.ok().entity(squadPlayerList).build();
    }
}