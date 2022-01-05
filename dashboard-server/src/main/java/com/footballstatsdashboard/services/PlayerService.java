package com.footballstatsdashboard.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.footballstatsdashboard.api.model.CountryCodeMetadata;
import com.footballstatsdashboard.api.model.ImmutablePlayer;
import com.footballstatsdashboard.api.model.Player;
import com.footballstatsdashboard.api.model.club.Club;
import com.footballstatsdashboard.api.model.player.Ability;
import com.footballstatsdashboard.api.model.player.Attribute;
import com.footballstatsdashboard.api.model.player.ImmutableAbility;
import com.footballstatsdashboard.api.model.player.ImmutableAttribute;
import com.footballstatsdashboard.api.model.player.ImmutableMetadata;
import com.footballstatsdashboard.api.model.player.Metadata;
import com.footballstatsdashboard.core.utils.FixtureLoader;
import com.footballstatsdashboard.db.CouchbaseDAO;
import com.footballstatsdashboard.db.key.ResourceKey;
import com.google.common.collect.ImmutableList;
import io.dropwizard.jackson.Jackson;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.footballstatsdashboard.core.utils.Constants.PLAYER_ATTRIBUTE_CATEGORY_MAP;

public class PlayerService {
    // TODO: 1/4/2022 remove suppression if and when we switch to using enum for attribute categories since we can
    //  infer the number of categories from it
    private static final int NUMBER_OF_ATTRIBUTE_CATEGORIES = 3;
    private static final FixtureLoader FIXTURE_LOADER = new FixtureLoader(Jackson.newObjectMapper().copy());

    private final CouchbaseDAO<ResourceKey> couchbaseDAO;

    public PlayerService(CouchbaseDAO<ResourceKey> couchbaseDAO) {
        this.couchbaseDAO = couchbaseDAO;
    }

    public Player getPlayer(UUID playerId) {
        ResourceKey resourceKey = new ResourceKey(playerId);
        return this.couchbaseDAO.getDocument(resourceKey, Player.class);
    }

    public Player createPlayer(Player incomingPlayer, Club clubDataForNewPlayer, String createdBy) throws IOException {
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

        Integer currentAbility = calculateCurrentAbility(newPlayerAttributes);
        // TODO: 1/4/2022 throw error if current ability is null
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

        Integer currentAbility = calculateCurrentAbility(updatedPlayerAttributes);
        // TODO: 1/4/2022 throw error if current ability is null
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
        this.couchbaseDAO.updateDocument(resourceKey, updatedPlayer);
        return updatedPlayer;
    }

    // TODO: 1/3/2022 add checks to make sure player being deleted belongs to the club and user making the request
    public void deletePlayer(UUID playerId) {
        ResourceKey resourceKey = new ResourceKey(playerId);
        this.couchbaseDAO.deleteDocument(resourceKey);
    }

    private Integer calculateCurrentAbility(List<Attribute> playerAttributes) {
        double meanTechnicalAbility =  playerAttributes.stream()
                .filter(attribute -> "Technical".equals(attribute.getCategory()))
                .mapToInt(Attribute::getValue)
                .average()
                .orElse(Double.NaN);

        double meanPhysicalAbility = playerAttributes.stream()
                .filter(attribute -> "Physical".equals(attribute.getCategory()))
                .mapToInt(Attribute::getValue)
                .average()
                .orElse(Double.NaN);

        double meanMentalAbility = playerAttributes.stream()
                .filter(attribute -> "Mental".equals(attribute.getCategory()))
                .mapToInt(Attribute::getValue)
                .average()
                .orElse(Double.NaN);
        if (Double.isNaN(meanTechnicalAbility) || Double.isNaN(meanPhysicalAbility)
                || Double.isNaN(meanMentalAbility)) {
            return null;
        }
        return Math.toIntExact(Math.round(
                (meanTechnicalAbility + meanPhysicalAbility + meanPhysicalAbility) / NUMBER_OF_ATTRIBUTE_CATEGORIES
        ));
    }
}