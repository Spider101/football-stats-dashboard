package com.footballstatsdashboard.resources;

import com.footballstatsdashboard.api.model.ImmutablePlayer;
import com.footballstatsdashboard.api.model.Player;
import com.footballstatsdashboard.api.model.User;
import com.footballstatsdashboard.api.model.club.Club;
import com.footballstatsdashboard.api.model.player.Attribute;
import com.footballstatsdashboard.api.model.player.ImmutableAttribute;
import com.footballstatsdashboard.api.model.player.ImmutableMetadata;
import com.footballstatsdashboard.api.model.player.Metadata;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.footballstatsdashboard.core.utils.Constants.PLAYER_ATTRIBUTE_CATEGORY_MAP;
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
        Player player = this.couchbaseDAO.getDocument(resourceKey, Player.class);
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
        ResourceKey resourceKeyForClub = new ResourceKey(incomingPlayer.getClubId());
        Club existingClub = this.couchbaseDAO.getDocument(resourceKeyForClub, Club.class);
        Metadata incomingPlayerMetadata = incomingPlayer.getMetadata();
        Metadata newPlayerMetadata = ImmutableMetadata.builder()
                .from(incomingPlayerMetadata)
                .club(existingClub.getName())
                .clubLogo("") // TODO: add club logo field here after updating club entity to include it
                .countryLogo("") // TODO: populate this correctly after implementing client for country flag look up api
                .build();

        List<Attribute> newPlayerAttributes = incomingPlayer.getAttributes().stream()
                .map(attribute -> {
                    Pair<String, String> categoryAndGroupNamePair =
                            PLAYER_ATTRIBUTE_CATEGORY_MAP.get(attribute.getName());
                    return ImmutableAttribute.builder()
                            .from(attribute)
                            .category(categoryAndGroupNamePair.getLeft())
                            .group(categoryAndGroupNamePair.getRight())
                            .history(Collections.singletonList(attribute.getValue()))
                            .build();
                })
                .collect(Collectors.toList());

        LocalDate currentDate = LocalDate.now();
        Player newPlayer = ImmutablePlayer.builder()
                .from(incomingPlayer)
                .metadata(newPlayerMetadata)
                .attributes(newPlayerAttributes)
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
        Player existingPlayer = this.couchbaseDAO.getDocument(resourceKey, Player.class);

        // incoming player's basic details should match with that of the existing player
        if (existingPlayer.getId().equals(incomingPlayer.getId())) {

            // TODO: 15/04/21 add validations by checking incoming player data against existing one
            List<Attribute> updatedPlayerAttributes = incomingPlayer.getAttributes().stream()
                    .map(incomingAttribute -> {
                        Attribute existingPlayerAttribute = existingPlayer.getAttributes().stream()
                                .filter(attribute -> attribute.getName().equals(incomingAttribute.getName()))
                                .findFirst().orElse(null);
                        if (existingPlayerAttribute != null) {
                            return ImmutableAttribute.builder()
                                    .from(existingPlayerAttribute)
                                    .name(incomingAttribute.getName())
                                    .value(incomingAttribute.getValue())
                                    .addHistory(incomingAttribute.getValue())
                                    .build();
                        }
                        // TODO: 1/2/2022 figure out if an error should be thrown if existing player attribute is null
                        return incomingAttribute;
                    })
                    .collect(Collectors.toList());
            ImmutablePlayer.Builder updatedPlayerBuilder = ImmutablePlayer.builder()
                    .from(existingPlayer)
                    .metadata(incomingPlayer.getMetadata())
                    .ability(incomingPlayer.getAbility())
                    .roles(incomingPlayer.getRoles())
                    .attributes(updatedPlayerAttributes)
                    .lastModifiedDate(LocalDate.now())
                    .createdBy(user.getEmail());

            Player updatedPlayer = updatedPlayerBuilder.build();
            this.couchbaseDAO.updateDocument(resourceKey, updatedPlayer);
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

        ResourceKey resourceKey = new ResourceKey(playerId);
        this.couchbaseDAO.deleteDocument(resourceKey);

        return Response.noContent().build();
    }
}