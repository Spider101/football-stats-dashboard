package com.footballstatsdashboard.services;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.footballstatsdashboard.api.model.CountryCodeMetadata;
import com.footballstatsdashboard.api.model.ImmutablePlayer;
import com.footballstatsdashboard.api.model.Player;
import com.footballstatsdashboard.api.model.Club;
import com.footballstatsdashboard.api.model.player.Ability;
import com.footballstatsdashboard.api.model.player.Attribute;
import com.footballstatsdashboard.api.model.player.ImmutableAbility;
import com.footballstatsdashboard.api.model.player.ImmutableAttribute;
import com.footballstatsdashboard.api.model.player.ImmutableMetadata;
import com.footballstatsdashboard.api.model.player.Metadata;
import com.footballstatsdashboard.core.exceptions.ServiceException;
import com.footballstatsdashboard.core.utils.FixtureLoader;
import com.footballstatsdashboard.core.validations.Validation;
import com.footballstatsdashboard.core.validations.ValidationSeverity;
import com.footballstatsdashboard.db.CouchbaseDAO;
import com.footballstatsdashboard.db.key.ResourceKey;
import com.google.common.collect.ImmutableList;
import io.dropwizard.jackson.Jackson;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.footballstatsdashboard.core.utils.Constants.PLAYER_ATTRIBUTE_CATEGORY_MAP;

public class PlayerService {
    // TODO: 1/4/2022 remove constant if and when we switch to using enum for attribute categories
    private static final List<String> ATTRIBUTE_CATEGORIES = ImmutableList.of("Technical", "Mental", "Physical");
    private static final FixtureLoader FIXTURE_LOADER = new FixtureLoader(Jackson.newObjectMapper().copy());
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerService.class);

    private final CouchbaseDAO<ResourceKey> playerDAO;

    public PlayerService(CouchbaseDAO<ResourceKey> playerDAO) {
        this.playerDAO = playerDAO;
    }

    public Player getPlayer(UUID playerId) {
        ResourceKey resourceKey = new ResourceKey(playerId);
        return fetchPlayerData(playerId, resourceKey);
    }

    public Player createPlayer(Player incomingPlayer, Club clubDataForNewPlayer, String createdBy) throws IOException {
        List<Validation> validationList = validateIncomingPlayer(incomingPlayer, null);
        if (!validationList.isEmpty()) {
            LOGGER.error("Unable to create new player! Found errors: {}", validationList);
            throw new ServiceException(HttpStatus.UNPROCESSABLE_ENTITY_422, "Invalid incoming player data",
                    validationList);
        }

        // process and persist the player data if no validations found
        TypeReference<List<CountryCodeMetadata>> countryCodeMetadataTypeRef = new TypeReference<>() { };
        List<CountryCodeMetadata> countryCodeMetadataList =
                FIXTURE_LOADER.loadFixture("countryCodeMapping.json", countryCodeMetadataTypeRef);
        String countryLogo = countryCodeMetadataList.stream()
                .filter(countryCodeMetadata ->
                        countryCodeMetadata.getCountryName().equals(incomingPlayer.getMetadata().getCountry()))
                .findFirst()
                .map(countryCodeMetadata ->
                        String.format("https://flagcdn.com/w40/%s.png", countryCodeMetadata.getCountryCode()))
                .orElse("");

        // add club information and other derived information to the metadata entity
        Metadata newPlayerMetadata = ImmutableMetadata.builder()
                .from(incomingPlayer.getMetadata())
                .club(clubDataForNewPlayer.getName())
                .clubLogo("") // TODO: add club logo field here after updating club entity to include it
                .countryLogo(countryLogo)
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

        // validate player attributes after adding category information
        ATTRIBUTE_CATEGORIES.forEach(attributeCategory -> {
            if (newPlayerAttributes.stream()
                    .noneMatch(attribute -> attributeCategory.equals(attribute.getCategory()))) {
                validationList.add(new Validation(ValidationSeverity.ERROR,
                        String.format("Player has no attributes associated to %s category", attributeCategory)));
            }
        });

        // early exit by throwing exception if any validations against the player attributes fail
        if (!validationList.isEmpty()) {
            String errorMessage = String.format("Player must have at least one attribute from each of %s categories",
                    ATTRIBUTE_CATEGORIES);
            LOGGER.error(errorMessage);
            throw new ServiceException(HttpStatus.UNPROCESSABLE_ENTITY_422, errorMessage, validationList);
        }

        Integer currentAbility = calculateCurrentAbility(newPlayerAttributes);
        Ability newPlayerAbility = ImmutableAbility.builder()
                .current(currentAbility)
                .history(ImmutableList.of(currentAbility))
                .build();

        LocalDate currentDate = LocalDate.now();
        Player newPlayer = ImmutablePlayer.builder()
                .from(incomingPlayer)
                .metadata(newPlayerMetadata)
                .ability(newPlayerAbility)
                .attributes(newPlayerAttributes)
                .createdBy(createdBy)
                .createdDate(currentDate)
                .lastModifiedDate(currentDate)
                .build();

        ResourceKey resourceKey = new ResourceKey(newPlayer.getId());
        this.playerDAO.insertDocument(resourceKey, newPlayer);

        return newPlayer;
    }

    public Player updatePlayer(Player incomingPlayer, Player existingPlayer, UUID playerId) {
        List<Validation> validationList = validateIncomingPlayer(incomingPlayer, existingPlayer);
        if (!validationList.isEmpty()) {
            LOGGER.error("Unable to create new player! Found errors: {}", validationList);
            throw new ServiceException(HttpStatus.UNPROCESSABLE_ENTITY_422, "Invalid incoming player data",
                    validationList);
        }

        // process and persist the player data if no validations found
        List<Attribute> updatedPlayerAttributes = incomingPlayer.getAttributes().stream()
                .map(incomingAttribute -> existingPlayer.getAttributes().stream()
                        .filter(attribute -> attribute.getName().equals(incomingAttribute.getName()))
                        .map(existingPlayerAttribute ->  ImmutableAttribute.builder()
                                .from(existingPlayerAttribute)
                                .name(incomingAttribute.getName())
                                .value(incomingAttribute.getValue())
                                .addHistory(incomingAttribute.getValue())
                                .build()
                        ).collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        Integer currentAbility = calculateCurrentAbility(updatedPlayerAttributes);
        Ability updatedPlayerAbility = ImmutableAbility.builder()
                .from(Objects.requireNonNull(existingPlayer.getAbility()))
                .current(currentAbility)
                .addHistory(currentAbility)
                .build();

        ImmutablePlayer.Builder updatedPlayerBuilder = ImmutablePlayer.builder()
                .from(existingPlayer)
                .metadata(incomingPlayer.getMetadata())
                .ability(updatedPlayerAbility)
                .roles(incomingPlayer.getRoles())
                .attributes(updatedPlayerAttributes)
                .lastModifiedDate(LocalDate.now());

        Player updatedPlayer = updatedPlayerBuilder.build();

        ResourceKey resourceKey = new ResourceKey(playerId);
        this.playerDAO.updateDocument(resourceKey, updatedPlayer);
        return updatedPlayer;
    }

    public void deletePlayer(UUID playerId, Predicate<UUID> doesClubBelongToUser) {
        ResourceKey resourceKey = new ResourceKey(playerId);

        // verify that the current user has access to the player they are trying to delete
        // since a player cannot belong to more than one club, we can transitively check the user's access to the player
        // by checking their access to the club the user belongs to
        Player player = fetchPlayerData(playerId, resourceKey);
        if (!doesClubBelongToUser.test(player.getClubId())) {
            LOGGER.error("Player with ID: {} does not belong to user making request", player.getId());
            throw new ServiceException(HttpStatus.FORBIDDEN_403, "User does not have access to this player!");
        }

        try {
            this.playerDAO.deleteDocument(resourceKey);
        } catch (DocumentNotFoundException documentNotFoundException) {
            LOGGER.error("No player entity found for ID: {}", playerId);
            throw new ServiceException(HttpStatus.NOT_FOUND_404,
                    String.format("Cannot delete player (ID: %s) that does not exist", playerId));
        }
    }

    private List<Validation> validateIncomingPlayer(Player incomingPlayer, Player existingPlayer) {
        List<Validation> validationList = new ArrayList<>();
        if (incomingPlayer.getRoles().size() == 0) {
            validationList.add(
                    new Validation(ValidationSeverity.ERROR, "Player must have list of roles associated with it!")
            );
        }

        if (incomingPlayer.getAttributes().size() == 0) {
            validationList.add(
                    new Validation(ValidationSeverity.ERROR, "Player must have list of associated attributes!")
            );
        }

        // validate that the attribute names for the incoming player data matches the existing player data
        if (existingPlayer != null) {
            List<String> existingPlayerAttributeNames = existingPlayer.getAttributes().stream()
                    .map(Attribute::getName)
                    .collect(Collectors.toList());
            if (incomingPlayer.getAttributes().stream()
                    .anyMatch(attribute -> !existingPlayerAttributeNames.contains(attribute.getName()))) {
                validationList.add(new Validation(ValidationSeverity.ERROR,
                        "Incoming Player attribute names must match existing player data"));
            }
        }
        return validationList;
    }

    private Integer calculateCurrentAbility(List<Attribute> playerAttributes) {
        List<OptionalDouble> meanAttributeByCategories = ATTRIBUTE_CATEGORIES.stream()
                .map(attributeCategory -> playerAttributes.stream()
                        .filter(attribute -> attributeCategory.equals(attribute.getCategory()))
                        .mapToInt(Attribute::getValue)
                        .average())
                .collect(Collectors.toList());
        OptionalDouble weightedMeanAttributes = meanAttributeByCategories.stream()
                // this should be true always because we throw validation unless there is at least one attribute for
                // each category
                .filter(OptionalDouble::isPresent)
                .mapToDouble(OptionalDouble::getAsDouble)
                .average();
        if (weightedMeanAttributes.isPresent()) {
            return (int) weightedMeanAttributes.getAsDouble();
        } else {
            // this else block should never be entered because we throw validation unless there is at least one
            // attribute for each category; investigate if it does
            LOGGER.error("Unable to calculate the current ability of the player from its attributes {}",
                    playerAttributes);
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR_500,
                    "Something went wrong trying to calculate current ability of player");
        }
    }

    private Player fetchPlayerData(UUID playerId, ResourceKey resourceKey) {
        try {
            return this.playerDAO.getDocument(resourceKey, Player.class);
        } catch (DocumentNotFoundException documentNotFoundException) {
            String errorMessage = String.format("No player entity found for ID: %s", playerId);
            LOGGER.error(errorMessage);
            throw new ServiceException(HttpStatus.NOT_FOUND_404, errorMessage);
        }
    }
}