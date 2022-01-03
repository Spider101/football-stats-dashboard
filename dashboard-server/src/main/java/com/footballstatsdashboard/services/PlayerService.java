package com.footballstatsdashboard.services;

import com.footballstatsdashboard.api.model.ImmutablePlayer;
import com.footballstatsdashboard.api.model.Player;
import com.footballstatsdashboard.api.model.club.Club;
import com.footballstatsdashboard.api.model.player.Attribute;
import com.footballstatsdashboard.api.model.player.ImmutableAttribute;
import com.footballstatsdashboard.api.model.player.ImmutableMetadata;
import com.footballstatsdashboard.api.model.player.Metadata;
import com.footballstatsdashboard.db.CouchbaseDAO;
import com.footballstatsdashboard.db.key.ResourceKey;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.footballstatsdashboard.core.utils.Constants.PLAYER_ATTRIBUTE_CATEGORY_MAP;

public class PlayerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerService.class);

    private final CouchbaseDAO<ResourceKey> couchbaseDAO;
    public PlayerService(CouchbaseDAO<ResourceKey> couchbaseDAO) {
        this.couchbaseDAO = couchbaseDAO;
    }

    public Player getPlayer(UUID playerId) {
        ResourceKey resourceKey = new ResourceKey(playerId);
        return this.couchbaseDAO.getDocument(resourceKey, Player.class);
    }

    public Player createPlayer(Player incomingPlayer, String createdBy) {
        // fetch details of club the incoming player belongs to
        ResourceKey resourceKeyForClub = new ResourceKey(incomingPlayer.getClubId());
        Club existingClub = this.couchbaseDAO.getDocument(resourceKeyForClub, Club.class);

        // add club information and other derived information to the metadata entity
        Metadata newPlayerMetadata = ImmutableMetadata.builder()
                .from(incomingPlayer.getMetadata())
                .club(existingClub.getName())
                .clubLogo("") // TODO: add club logo field here after updating club entity to include it
                .countryLogo("") // TODO: populate this correctly after implementing client for country flag look up api
                .build();

        // add category and group information to each attribute on the player entity
        // also, initialize the attribute history with the current value
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

        // TODO: calculate the player's current ability on the basis of the attributes
        //  also initialize the ability history with that value

        LocalDate currentDate = LocalDate.now();
        Player newPlayer = ImmutablePlayer.builder()
                .from(incomingPlayer)
                .metadata(newPlayerMetadata)
                .attributes(newPlayerAttributes)
                .createdBy(createdBy)
                .createdDate(currentDate)
                .lastModifiedDate(currentDate)
                .build();

        ResourceKey resourceKey = new ResourceKey(newPlayer.getId());
        this.couchbaseDAO.insertDocument(resourceKey, newPlayer);

        return newPlayer;
    }

    public Player updatePlayer(Player incomingPlayer, Player existingPlayer, UUID playerId) {
        ResourceKey resourceKey = new ResourceKey(playerId);

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
                .lastModifiedDate(LocalDate.now());

        Player updatedPlayer = updatedPlayerBuilder.build();
        this.couchbaseDAO.updateDocument(resourceKey, updatedPlayer);
        return updatedPlayer;
    }

    // TODO: 1/3/2022 add checks to make sure player being deleted belongs to the club and user making the request
    public void deletePlayer(UUID playerId) {
        ResourceKey resourceKey = new ResourceKey(playerId);
        this.couchbaseDAO.deleteDocument(resourceKey);
    }
}