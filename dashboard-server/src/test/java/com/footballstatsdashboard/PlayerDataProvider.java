package com.footballstatsdashboard;

import com.footballstatsdashboard.api.model.ImmutablePlayer;
import com.footballstatsdashboard.api.model.Player;
import com.footballstatsdashboard.api.model.player.Ability;
import com.footballstatsdashboard.api.model.player.Attribute;
import com.footballstatsdashboard.api.model.player.AttributeCategory;
import com.footballstatsdashboard.api.model.player.AttributeGroup;
import com.footballstatsdashboard.api.model.player.ImmutableAbility;
import com.footballstatsdashboard.api.model.player.ImmutableAttribute;
import com.footballstatsdashboard.api.model.player.ImmutableMetadata;
import com.footballstatsdashboard.api.model.player.ImmutableRole;
import com.footballstatsdashboard.api.model.player.Metadata;
import com.footballstatsdashboard.api.model.player.Role;
import com.google.common.collect.ImmutableList;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// TODO: 1/9/2022 set `history` properties to null when isExisting is false
public class PlayerDataProvider {
    private static final int PLAYER_AGE = 27;
    private static final int CURRENT_PLAYER_ABILITY = 19;
    private static final int CURRENT_PLAYER_SPRINT_SPEED = 85;
    private static final int CURRENT_PLAYER_CROSSING = 80;
    private static final int CURRENT_PLAYER_COMPOSURE = 75;
    private static final String CREATED_BY = "fake email";

    /**
     * Helps in building a player entity according to the needs of a test
     */
    public static final class PlayerBuilder {
        private boolean isExistingPlayer = false;
        private final ImmutablePlayer.Builder basePlayer = ImmutablePlayer.builder().clubId(UUID.randomUUID());

        private PlayerBuilder() { }

        public static PlayerBuilder builder() {
            return new PlayerBuilder();
        }

        public PlayerBuilder withId(UUID playerId) {
            basePlayer.id(playerId);
            return this;
        }

        public PlayerBuilder isExistingPlayer(boolean isExisting) {
            this.isExistingPlayer = isExisting;
            return this;
        }

        public PlayerBuilder withAbility() {
            Ability playerAbility = ImmutableAbility.builder()
                    .current(CURRENT_PLAYER_ABILITY)
                    .history(isExistingPlayer ? ImmutableList.of(CURRENT_PLAYER_ABILITY) : ImmutableList.of())
                    .build();
            basePlayer.ability(playerAbility);
            return this;
        }
        public PlayerBuilder withMetadata() {
            Metadata playerMetadata = ImmutableMetadata.builder()
                    .name("fake player name")
                    .country("fake country name")
                    .age(PLAYER_AGE)
                    .club(isExistingPlayer ? "fake club name" : null)
                    .clubLogo(isExistingPlayer ? "fake club logo" : null)
                    .countryLogo(isExistingPlayer ? "fake country logo" : null)
                    .build();
            basePlayer.metadata(playerMetadata);
            return this;
        }

        public PlayerBuilder withRoles() {
            Role playerRole = ImmutableRole.builder()
                    .name("player role")
                    .build();
            basePlayer.roles(ImmutableList.of(playerRole));
            return this;
        }

        // TODO: 15/04/22 remove this (unused)
        public PlayerBuilder withTechnicalAttributes() {
            Attribute technicalAttribute = ImmutableAttribute.builder()
                    .name("crossing")
                    .value(CURRENT_PLAYER_CROSSING)
                    .category(isExistingPlayer ? AttributeCategory.TECHNICAL : null)
                    .group(isExistingPlayer ? AttributeGroup.ATTACKING : null)
                    .history(isExistingPlayer ? ImmutableList.of(CURRENT_PLAYER_CROSSING) : ImmutableList.of())
                    .build();
            basePlayer.addAttributes(technicalAttribute);
            return this;
        }

        public PlayerBuilder withPhysicalAttributes() {
            Attribute physicalAttribute = ImmutableAttribute.builder()
                    .name("sprintSpeed")
                    .value(CURRENT_PLAYER_SPRINT_SPEED)
                    .category(isExistingPlayer ? AttributeCategory.PHYSICAL : null)
                    .group(isExistingPlayer ? AttributeGroup.SPEED : null)
                    .history(isExistingPlayer ? ImmutableList.of(CURRENT_PLAYER_SPRINT_SPEED) : ImmutableList.of())
                    .build();
            basePlayer.addAttributes(physicalAttribute);
            return this;
        }

        public PlayerBuilder withMentalAttributes() {
            Attribute mentalAttribute = ImmutableAttribute.builder()
                    .name("composure")
                    .value(CURRENT_PLAYER_COMPOSURE)
                    .category(isExistingPlayer ? AttributeCategory.MENTAL : null)
                    .group(isExistingPlayer ? AttributeGroup.ATTACKING : null)
                    .history(isExistingPlayer ? ImmutableList.of(CURRENT_PLAYER_COMPOSURE) : ImmutableList.of())
                    .build();
            basePlayer.addAttributes(mentalAttribute);
            return this;
        }

        public PlayerBuilder withInvalidAttributes() {
            Attribute invalidAttribute = ImmutableAttribute.builder()
                    .name("fake attribute name")
                    .value(Integer.valueOf("45"))
                    .build();
            basePlayer.addAttributes(invalidAttribute);
            return this;
        }

        public PlayerBuilder withAttributes() {
            Attribute technicalAttribute = ImmutableAttribute.builder()
                    .name("crossing")
                    .value(CURRENT_PLAYER_CROSSING)
                    .category(isExistingPlayer ? AttributeCategory.TECHNICAL : null)
                    .group(isExistingPlayer ? AttributeGroup.ATTACKING : null)
                    .history(isExistingPlayer ? ImmutableList.of(CURRENT_PLAYER_CROSSING) : ImmutableList.of())
                    .build();

            Attribute physicalAttribute = ImmutableAttribute.builder()
                    .name("sprintSpeed")
                    .value(CURRENT_PLAYER_SPRINT_SPEED)
                    .category(isExistingPlayer ? AttributeCategory.PHYSICAL : null)
                    .group(isExistingPlayer ? AttributeGroup.SPEED : null)
                    .history(isExistingPlayer ? ImmutableList.of(CURRENT_PLAYER_SPRINT_SPEED) : ImmutableList.of())
                    .build();

            Attribute mentalAttribute = ImmutableAttribute.builder()
                    .name("composure")
                    .value(CURRENT_PLAYER_COMPOSURE)
                    .category(isExistingPlayer ? AttributeCategory.MENTAL : null)
                    .group(isExistingPlayer ? AttributeGroup.ATTACKING : null)
                    .history(isExistingPlayer ? ImmutableList.of(CURRENT_PLAYER_COMPOSURE) : ImmutableList.of())
                    .build();
            basePlayer.addAttributes(technicalAttribute, physicalAttribute, mentalAttribute);
            return this;
        }

        public Player build() {
            // add some house-keeping fields if it is an existing player
            if (isExistingPlayer) {
                Instant currentInstant = Instant.now();
                Instant olderInstant = currentInstant.minus(1, ChronoUnit.DAYS);

                basePlayer.lastModifiedDate(LocalDate.ofInstant(olderInstant, ZoneId.systemDefault()));
                basePlayer.createdBy(CREATED_BY);
            }
            return this.basePlayer.build();
        }
    }

    /**
     * Helps in building a player entity from an existing one and allows overriding certain properties according to the
     * needs of a test
     */
    public static final class ModifiedPlayerBuilder {
        private Player playerReference;
        private ImmutablePlayer.Builder basePlayer = ImmutablePlayer.builder();

        private ModifiedPlayerBuilder() { }

        public static ModifiedPlayerBuilder builder() {
            return new ModifiedPlayerBuilder();
        }

        public ModifiedPlayerBuilder from(Player player) {
            this.playerReference = player;
            basePlayer = ImmutablePlayer.builder().from(player);
            return this;
        }

        public ModifiedPlayerBuilder withUpdatedNameInMetadata(String newPlayerName) {
            Metadata updatedMetadata = ImmutableMetadata.builder()
                    .from(this.playerReference.getMetadata())
                    .name(newPlayerName)
                    .build();
            basePlayer.metadata(updatedMetadata);
            return this;
        }

        public ModifiedPlayerBuilder withUpdatedCurrentAbility(int newCurrentAbility) {
            ImmutableAbility.Builder updatedAbilityBuilder = ImmutableAbility.builder();
            if (this.playerReference.getAbility() != null) {
                updatedAbilityBuilder.from(this.playerReference.getAbility());
            }
            Ability updatedAbility = updatedAbilityBuilder
                    .current(newCurrentAbility)
                    .addHistory(newCurrentAbility)
                    .build();
            basePlayer.ability(updatedAbility);
            return this;
        }

        public ModifiedPlayerBuilder withUpdatedRoleName(String newRoleName) {
            List<Role> updatedPlayerRoles = this.playerReference.getRoles().stream()
                    .map(role -> ImmutableRole.builder()
                            .from(role)
                            .name(newRoleName)
                            .build()
                    ).collect(Collectors.toList());
            basePlayer.roles(updatedPlayerRoles);
            return this;
        }

        public ModifiedPlayerBuilder withUpdatedAttributeValue(String attributeName, int newAttributeValue) {
            List<Attribute> updatedAttributes = this.playerReference.getAttributes().stream()
                    .map(attribute -> {
                        if (attributeName.equals(attribute.getName())) {
                            return ImmutableAttribute.builder()
                                    .from(attribute)
                                    .value(newAttributeValue)
                                    .addHistory(newAttributeValue)
                                    .build();
                        }
                        return attribute;
                    }).collect(Collectors.toList());
            basePlayer.attributes(updatedAttributes);
            return this;
        }

        public Player build() {
            return this.basePlayer
                    .lastModifiedDate(LocalDate.now())
                    .build();
        }
    }
}