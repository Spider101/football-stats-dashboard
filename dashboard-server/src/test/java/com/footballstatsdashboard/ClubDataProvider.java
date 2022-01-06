package com.footballstatsdashboard;

import com.footballstatsdashboard.api.model.club.Club;
import com.footballstatsdashboard.api.model.club.ImmutableClub;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class ClubDataProvider {
    private static final String CREATED_BY = "fake email";
    private static final String DEFAULT_CLUB_NAME = "fake club name";
    private static final int NUMBER_OF_CLUBS = 3;

    private ClubDataProvider() { }
    /**
     * Helps in building a club entity according to the needs of a test
     */
    public static final class ClubBuilder {
        private boolean isExistingClub = false;
        private String customClubName = null;
        private UUID existingUserId = null;
        private final ImmutableClub.Builder baseClub = ImmutableClub.builder();

        private ClubBuilder() { }

        public static ClubBuilder builder() {
            return new ClubBuilder();
        }

        public ClubBuilder isExisting(boolean isExisting) {
            this.isExistingClub = isExisting;
            return this;
        }

        public ClubBuilder withId(UUID clubId) {
            baseClub.id(clubId);
            return this;
        }

        public ClubBuilder customClubName(String clubName) {
            this.customClubName = clubName;
            return this;
        }

        public ClubBuilder existingUserId(UUID userId) {
            this.existingUserId = userId;
            return this;
        }

        public Club build() {
            // add some house-keeping fields if it is an existing club
            if (isExistingClub) {
                Instant currentInstant = Instant.now();
                Instant olderInstant = currentInstant.minus(1, ChronoUnit.DAYS);

                baseClub.lastModifiedDate(LocalDate.ofInstant(olderInstant, ZoneId.systemDefault()));
                baseClub.createdBy(CREATED_BY);
                baseClub.userId(this.existingUserId != null ? existingUserId : UUID.randomUUID());
            }

            // add the required fields which don't require dynamic values in test suites and build the club entity
            return this.baseClub
                    .name(this.customClubName != null ? this.customClubName : DEFAULT_CLUB_NAME)
                    .expenditure(new BigDecimal("1000"))
                    .income(new BigDecimal("2000"))
                    .transferBudget(new BigDecimal("500"))
                    .wageBudget(new BigDecimal("200"))
                    .build();
        }
    }

    /**
     * Helps in building a club entity from an existing one and allows overriding certain properties according to the
     * needs of a test
     */
    public static final class ModifiedClubBuilder {
        private ImmutableClub.Builder baseClub = ImmutableClub.builder();

        private ModifiedClubBuilder() { }

        public static ModifiedClubBuilder builder() {
            return new ModifiedClubBuilder();
        }

        public ModifiedClubBuilder from(Club club) {
            baseClub = ImmutableClub.builder().from(club);
            return this;
        }

        public ModifiedClubBuilder withUpdatedWageBudget(BigDecimal newWageBudget) {
            baseClub.wageBudget(newWageBudget);
            return this;
        }

        public Club build() {
            return this.baseClub
                    .lastModifiedDate(LocalDate.now())
                    .build();
        }
    }

    /**
     * Leverage the ClubBuilder to create a fixed number of Club entities associated with the user whose ID is passed in
     * @param userId ID of the user whose associated club data needs to be fetched
     * @return a fixed number of Club entities associated with the user whose ID passed in
     */
    public static List<Club> getAllClubsForUser(UUID userId) {
        return IntStream.range(0, NUMBER_OF_CLUBS).mapToObj(i ->
                ClubBuilder.builder()
                        .isExisting(true)
                        .existingUserId(userId)
                        .customClubName(DEFAULT_CLUB_NAME + i)
                        .build()
        ).collect(Collectors.toList());
    }
}