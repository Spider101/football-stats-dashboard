package com.footballstatsdashboard;

import com.footballstatsdashboard.api.model.Club;
import com.footballstatsdashboard.api.model.club.ClubSummary;
import com.footballstatsdashboard.api.model.ImmutableClub;
import com.footballstatsdashboard.api.model.club.Expenditure;
import com.footballstatsdashboard.api.model.club.ImmutableClubSummary;
import com.footballstatsdashboard.api.model.club.ImmutableExpenditure;
import com.footballstatsdashboard.api.model.club.ImmutableIncome;
import com.footballstatsdashboard.api.model.club.Income;
import com.google.common.collect.ImmutableList;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class ClubDataProvider {
    public static final BigDecimal CURRENT_INCOME = new BigDecimal("1000");
    public static final BigDecimal CURRENT_EXPENDITURE = new BigDecimal("2000");
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

        public ClubBuilder withIncome() {
            Income clubIncome = ImmutableIncome.builder()
                    .current(CURRENT_INCOME)
                    .history(isExistingClub ? ImmutableList.of(CURRENT_INCOME) : ImmutableList.of())
                    .build();
            baseClub.income(clubIncome);
            return this;
        }

        public ClubBuilder withExpenditure() {
            Expenditure clubExpenditure = ImmutableExpenditure.builder()
                    .current(CURRENT_EXPENDITURE)
                    .history(isExistingClub ? ImmutableList.of(CURRENT_EXPENDITURE) : ImmutableList.of())
                    .build();
            baseClub.expenditure(clubExpenditure);
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
                baseClub.createdDate(LocalDate.ofInstant(currentInstant, ZoneId.systemDefault()));

                Instant olderInstant = currentInstant.minus(1, ChronoUnit.DAYS);
                baseClub.lastModifiedDate(LocalDate.ofInstant(olderInstant, ZoneId.systemDefault()));

                baseClub.createdBy(CREATED_BY);
                baseClub.userId(this.existingUserId != null ? existingUserId : UUID.randomUUID());
            }

            // add the required fields which don't require dynamic values in test suites and build the club entity
            return this.baseClub
                    .name(this.customClubName != null ? this.customClubName : DEFAULT_CLUB_NAME)
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
        private Club clubReference;

        private ModifiedClubBuilder() { }

        public static ModifiedClubBuilder builder() {
            return new ModifiedClubBuilder();
        }

        public ModifiedClubBuilder from(Club club) {
            clubReference = club;
            baseClub = ImmutableClub.builder().from(club);
            return this;
        }

        public ModifiedClubBuilder withUpdatedName(String newName) {
            baseClub.name(newName);
            return this;
        }

        public ModifiedClubBuilder withUpdatedWageBudget(BigDecimal newWageBudget) {
            baseClub.wageBudget(newWageBudget);
            return this;
        }

        public ModifiedClubBuilder withUpdatedIncome() {
            BigDecimal updatedIncomeValue = CURRENT_INCOME.add(new BigDecimal("1000"));
            Income updatedIncome = ImmutableIncome.builder()
                    .from(Objects.requireNonNull(clubReference.getIncome()))
                    .current(updatedIncomeValue)
                    .addHistory(updatedIncomeValue)
                    .build();
            baseClub.income(updatedIncome);
            return this;
        }

        public ModifiedClubBuilder withUpdatedExpenditure() {
            BigDecimal updatedExpenditureValue = CURRENT_EXPENDITURE.add(new BigDecimal("1000"));
            Expenditure updatedExpenditure = ImmutableExpenditure.builder()
                    .from(Objects.requireNonNull(clubReference.getExpenditure()))
                    .current(updatedExpenditureValue)
                    .addHistory(updatedExpenditureValue)
                    .build();
            baseClub.expenditure(updatedExpenditure);
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
    public static List<ClubSummary> getAllClubSummariesForUser(UUID userId) {
        return IntStream.range(0, NUMBER_OF_CLUBS).mapToObj(i -> {
            Club existingClub = ClubBuilder.builder()
                    .isExisting(true)
                    .existingUserId(userId)
                    .customClubName(DEFAULT_CLUB_NAME + i)
                    .withId(UUID.randomUUID())
                    .withIncome()
                    .withExpenditure()
                    .build();
            return ImmutableClubSummary.builder()
                    .clubId(existingClub.getId())
                    .name(existingClub.getName())
                    .createdDate(Objects.requireNonNull(existingClub.getCreatedDate()))
                    .build();
        }).collect(Collectors.toList());
    }
}