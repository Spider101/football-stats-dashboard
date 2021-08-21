package com.footballstatsdashboard.resources;

import com.footballstatsdashboard.api.model.ImmutableMatchPerformance;
import com.footballstatsdashboard.api.model.MatchPerformance;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.db.MatchPerformanceDAO;
import com.footballstatsdashboard.db.key.ResourceKey;
import io.dropwizard.auth.Auth;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
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
import java.util.HashMap;
import java.util.UUID;

import static com.footballstatsdashboard.core.utils.Constants.MATCH_PERFORMANCE_ID;
import static com.footballstatsdashboard.core.utils.Constants.MATCH_PERFORMANCE_ID_PATH;
import static com.footballstatsdashboard.core.utils.Constants.MATCH_PERFORMANCE_V1_BASE_PATH;
import static com.footballstatsdashboard.core.utils.Constants.PLAYER_ID;

@Path(MATCH_PERFORMANCE_V1_BASE_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class MatchPerformanceResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClubResource.class);

    private final MatchPerformanceDAO<ResourceKey> matchPerformanceDAO;

    public MatchPerformanceResource(MatchPerformanceDAO<ResourceKey> matchPerformanceDAO) {
        this.matchPerformanceDAO = matchPerformanceDAO;
    }

    @GET
    @Path(MATCH_PERFORMANCE_ID_PATH)
    public Response getMatchPerformance(
            @Auth @PathParam(MATCH_PERFORMANCE_ID) UUID matchPerformanceId) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("getMatchPerformance() request for match performance ID: {}", matchPerformanceId);
        }

        ResourceKey resourceKey = new ResourceKey(matchPerformanceId);
        MatchPerformance matchPerformance =
                this.matchPerformanceDAO.getDocument(resourceKey, MatchPerformance.class).getLeft();
        return Response.ok().entity(matchPerformance).build();
    }

    @POST
    public Response createMatchPerformance(
            @Auth User user,
            @Valid MatchPerformance incomingMatchPerformance,
            @Context UriInfo uriInfo) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("createMatchPerformance() request.");
        }

        // TODO: 17/04/21 add more internal data when business logic becomes complicated
        LocalDate currentDate = LocalDate.now();
        MatchPerformance newMatchPerformance = ImmutableMatchPerformance.builder()
                .from(incomingMatchPerformance)
                .createdDate(currentDate)
                .lastModifiedDate(currentDate)
                .createdBy(user.getEmail())
                .build();

        ResourceKey resourceKey = new ResourceKey(newMatchPerformance.getId());
        this.matchPerformanceDAO.insertDocument(resourceKey, newMatchPerformance);

        URI location = uriInfo.getAbsolutePathBuilder().path(newMatchPerformance.getId().toString()).build();
        return Response.created(location).entity(new HashMap<>()).build();
    }

    @PUT
    @Path(MATCH_PERFORMANCE_ID_PATH)
    public Response updateMatchPerformance(
            @Auth @PathParam(MATCH_PERFORMANCE_ID) UUID existingMatchPerformanceId,
            @Valid MatchPerformance incomingMatchPerformance) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("updateMatchPerformance() request for match performance ID: {}", existingMatchPerformanceId);
        }

        ResourceKey resourceKey = new ResourceKey(existingMatchPerformanceId);
        Pair<MatchPerformance, Long> existingMatchPerformanceEntity = this.matchPerformanceDAO.getDocument(resourceKey,
                MatchPerformance.class);
        MatchPerformance existingMatchPerformance = existingMatchPerformanceEntity.getLeft();

        if (existingMatchPerformance.getId().equals(incomingMatchPerformance.getId())) {
            MatchPerformance updatedMatchPerformance = ImmutableMatchPerformance.builder()
                    .from(existingMatchPerformance)
                    .appearances(incomingMatchPerformance.getAppearances())
                    .goals(incomingMatchPerformance.getGoals())
                    .dribbles(incomingMatchPerformance.getDribbles())
                    .passCompletionRate(incomingMatchPerformance.getPassCompletionRate())
                    .assists(incomingMatchPerformance.getAssists())
                    .yellowCards(incomingMatchPerformance.getYellowCards())
                    .redCards(incomingMatchPerformance.getRedCards())
                    .tackles(incomingMatchPerformance.getTackles())
                    .matchRating(incomingMatchPerformance.getMatchRating())
                    .playerOfTheMatch(incomingMatchPerformance.getPlayerOfTheMatch())
                    .penalties(incomingMatchPerformance.getPenalties())
                    .fouls(incomingMatchPerformance.getFouls())
                    .lastModifiedDate(LocalDate.now())
                    .build();

            this.matchPerformanceDAO.updateDocument(resourceKey, updatedMatchPerformance,
                    existingMatchPerformanceEntity.getRight());
            return Response.ok().entity(updatedMatchPerformance).build();
        }

        LOGGER.error("Incoming match performance entity ID: {} does not matching ID of existing match performance " +
                        "entity in couchbase: {}. Aborting operation!",
                incomingMatchPerformance.getId(), existingMatchPerformance.getId());
        return Response.serverError()
                .entity("Unable to update match performance with ID: " + incomingMatchPerformance.getId().toString())
                .build();
    }

    @DELETE
    @Path(MATCH_PERFORMANCE_ID_PATH)
    public Response deleteMatchPerformance(
            @Auth @PathParam(MATCH_PERFORMANCE_ID) UUID matchPerformanceId) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("deleteMatchPerformance() request for match performance entity with ID: {}",
                    matchPerformanceId);
        }

        ResourceKey resourceKey = new ResourceKey(matchPerformanceId);
        this.couchbaseDAO.deleteDocument(resourceKey);

        return Response.noContent().build();
    }
}