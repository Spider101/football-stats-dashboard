package com.footballstatsdashboard.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.footballstatsdashboard.api.model.CountryCodeMetadata;
import com.footballstatsdashboard.api.model.ImmutablePlayer;
import com.footballstatsdashboard.api.model.Player;
import com.footballstatsdashboard.api.model.Club;
import com.footballstatsdashboard.api.model.player.Ability;
import com.footballstatsdashboard.api.model.player.Attribute;
import com.footballstatsdashboard.api.model.player.AttributeCategory;
import com.footballstatsdashboard.api.model.player.AttributeGroup;
import com.footballstatsdashboard.api.model.player.ImmutableAbility;
import com.footballstatsdashboard.api.model.player.ImmutableAttribute;
import com.footballstatsdashboard.api.model.player.ImmutableMetadata;
import com.footballstatsdashboard.api.model.player.Metadata;
import com.footballstatsdashboard.core.exceptions.ServiceException;
import com.footballstatsdashboard.core.utils.FixtureLoader;
import com.footballstatsdashboard.core.validations.Validation;
import com.footballstatsdashboard.core.validations.ValidationSeverity;
import com.footballstatsdashboard.db.IPlayerEntityDAO;
import com.google.common.collect.ImmutableList;
import io.dropwizard.jackson.Jackson;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.footballstatsdashboard.core.utils.Constants.COUNTRY_CODE_MAPPING_FNAME;
import static com.footballstatsdashboard.core.utils.Constants.COUNTRY_FLAG_URL_TEMPLATE;
import static com.footballstatsdashboard.core.utils.Constants.PLAYER_ATTRIBUTE_CATEGORY_MAP;

public class PlayerService {
    private static final FixtureLoader FIXTURE_LOADER = new FixtureLoader(Jackson.newObjectMapper().copy());
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerService.class);

    private final IPlayerEntityDAO playerDAO;

    public PlayerService(IPlayerEntityDAO playerDAO) {
        this.playerDAO = playerDAO;
    }

    public Player getPlayer(UUID playerId) {
        try {
            return this.playerDAO.getEntity(playerId);
        } catch (EntityNotFoundException entityNotFoundException) {
            String errorMessage = String.format("No player entity found for ID: %s", playerId);
            LOGGER.error(errorMessage);
            throw new ServiceException(HttpStatus.NOT_FOUND_404, errorMessage);
        }
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
                FIXTURE_LOADER.loadFixture(COUNTRY_CODE_MAPPING_FNAME, countryCodeMetadataTypeRef);
        String countryLogo = countryCodeMetadataList.stream()
                .filter(countryCodeMetadata ->
                        countryCodeMetadata.getCountryName().equals(incomingPlayer.getMetadata().getCountry()))
                .findFirst()
                .map(countryCodeMetadata ->
                        String.format(COUNTRY_FLAG_URL_TEMPLATE, countryCodeMetadata.getCountryCode()))
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
                    Pair<AttributeCategory, AttributeGroup> categoryAndGroupNamePair =
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
        Arrays.stream(AttributeCategory.values()).forEach(attributeCategory -> {
            if (newPlayerAttributes.stream()
                    .noneMatch(attribute -> attributeCategory  == attribute.getCategory())) {
                validationList.add(new Validation(ValidationSeverity.ERROR,
                        String.format("Player has no attributes associated to %s category", attributeCategory)));
            }
        });

        // early exit by throwing exception if any validations against the player attributes fail
        if (!validationList.isEmpty()) {
            String errorMessage = "Something went wrong trying to set category for player attributes!";
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

        this.playerDAO.insertEntity(newPlayer);

        return newPlayer;
    }

    public Player updatePlayer(Player incomingPlayer, UUID existingPlayerId, UUID authorizedUserId) {
        Player existingPlayer = getPlayer(existingPlayerId);

        try {
            // verify that the current user has access to the player they are trying to delete
            if (!this.playerDAO.doesEntityBelongToUser(existingPlayerId, authorizedUserId)) {
                LOGGER.error("Player with ID: {} does not belong to user making request", existingPlayerId);
                throw new ServiceException(HttpStatus.FORBIDDEN_403, "User does not have access to this player!");
            }
        } catch (NoResultException noResultException) {
            LOGGER.error("Cannot update player with ID: {} that does not exist", existingPlayerId);
            throw new ServiceException(HttpStatus.NOT_FOUND_404,
                    String.format("No player entity found for ID: %s", existingPlayerId));
        }

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

        this.playerDAO.updateEntity(existingPlayerId, updatedPlayer);
        return updatedPlayer;
    }

    public void deletePlayer(UUID playerId, UUID authorizedUserId) {
        if (!this.playerDAO.doesEntityExist(playerId)) {
            String errorMessage = String.format("No player entity found for ID: %s", playerId);
            LOGGER.error(errorMessage);
            throw new ServiceException(HttpStatus.NOT_FOUND_404, errorMessage);
        }

        try {
            // verify that the current user has access to the player they are trying to delete
            if (!this.playerDAO.doesEntityBelongToUser(playerId, authorizedUserId)) {
                LOGGER.error("Player with ID: {} that does not belong to user making request", playerId);
                throw new ServiceException(HttpStatus.FORBIDDEN_403, "User does not have access to this player!");
            }
        } catch (NoResultException noResultException) {
            LOGGER.error("Cannot delete player with ID {} because it does not belong to any existing club associated" +
                    " with current user", playerId);
            throw new ServiceException(HttpStatus.FORBIDDEN_403, "User does not have access to this player!");
        }

        this.playerDAO.deleteEntity(playerId);
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
        List<OptionalDouble> meanAttributeByCategories = Arrays.stream(AttributeCategory.values())
                .map(attributeCategory -> playerAttributes.stream()
                        .filter(attribute -> attributeCategory == attribute.getCategory())
                        .mapToInt(Attribute::getValue)
                        .average()
                ).collect(Collectors.toList());
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
}