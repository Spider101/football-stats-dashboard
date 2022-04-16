package com.footballstatsdashboard;

import com.footballstatsdashboard.api.model.club.BoardObjective;
import com.footballstatsdashboard.api.model.club.ImmutableBoardObjective;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BoardObjectiveDataProvider {

    private static final int NUMBER_OF_BOARD_OBJECTIVES = 5;

    /**
     * Helps in building a board objective entity according to the needs of a test
     */
    public static final class BoardObjectiveBuilder {
        private boolean isExistingBoardObjective = false;
        private String customTitle = null;
        private String customDescription = null;
        private boolean isCompletedStatus = false;

        private static final String CREATED_BY = "fake email";

        private final ImmutableBoardObjective.Builder baseBoardObjective = ImmutableBoardObjective.builder();

        private BoardObjectiveBuilder() { }

        public static BoardObjectiveBuilder builder() {
            return new BoardObjectiveBuilder();
        }

        public BoardObjectiveBuilder withClubId(UUID clubId) {
            baseBoardObjective.clubId(clubId);
            return this;
        }

        public BoardObjectiveBuilder withExistingId(UUID existingBoardObjectiveId) {
            baseBoardObjective.id(existingBoardObjectiveId);
            return this;
        }

        public BoardObjectiveBuilder isExisting(boolean isExisting) {
            this.isExistingBoardObjective = isExisting;
            return this;
        }

        public BoardObjectiveBuilder customTitle(String objectiveTitle) {
            this.customTitle = objectiveTitle;
            return this;
        }

        public BoardObjectiveBuilder customDescription(String objectiveDescription) {
            this.customDescription = objectiveDescription;
            return this;
        }

        public BoardObjectiveBuilder toggleCompletionStatus() {
            this.isCompletedStatus = !this.isCompletedStatus;
            return this;
        }

        public BoardObjective build() {
            if (isExistingBoardObjective) {
                Instant currentInstant = Instant.now();
                baseBoardObjective.createdDate(LocalDate.ofInstant(currentInstant, ZoneId.systemDefault()));

                Instant olderInstant = currentInstant.minus(1, ChronoUnit.DAYS);
                baseBoardObjective.lastModifiedDate(LocalDate.ofInstant(olderInstant, ZoneId.systemDefault()));

                baseBoardObjective.createdBy(CREATED_BY);
            }
            return baseBoardObjective
                    .title(this.customTitle != null ? this.customTitle : "fake objective title")
                    .description(this.customDescription != null ? this.customDescription : "fake objective description")
                    .isCompleted(this.isCompletedStatus)
                    .build();
        }
    }

    /**
     * Helps in building a board objective entity from an existing one for the needs of a test
     */
    public static final class ModifiedBoardObjectiveBuilder {
        private boolean isForUpdatedEntity = false;
        private BoardObjective boardObjectiveReference;
        private final ImmutableBoardObjective.Builder baseBoardObjective = ImmutableBoardObjective.builder();

        private ModifiedBoardObjectiveBuilder() { }

        public static ModifiedBoardObjectiveBuilder builder() {
            return new ModifiedBoardObjectiveBuilder();
        }

        public ModifiedBoardObjectiveBuilder from(BoardObjective boardObjective) {
            this.boardObjectiveReference = boardObjective;
            baseBoardObjective.from(boardObjective);
            return this;
        }

        public ModifiedBoardObjectiveBuilder withUpdatedCompletionFlag() {
            baseBoardObjective.isCompleted(!this.boardObjectiveReference.getIsCompleted());
            return this;
        }

        public ModifiedBoardObjectiveBuilder isForUpdatedEntity(boolean isForUpdate) {
            this.isForUpdatedEntity = isForUpdate;
            return this;
        }

        public BoardObjective build() {
            if (isForUpdatedEntity) {
                baseBoardObjective.lastModifiedDate(LocalDate.now());
            }
            return baseBoardObjective.build();
        }
    }

    /**
     * get multiple board objective stubs with the same club ID
     * @param clubId ID of club the board objective belongs to
     * @return list of board objective stubs to use for testing
     */
    public static List<BoardObjective> getMultipleBoardObjectives(UUID clubId) {
        return IntStream.range(0, NUMBER_OF_BOARD_OBJECTIVES)
                .mapToObj(i ->
                        BoardObjectiveBuilder.builder()
                                .isExisting(true)
                                .withClubId(clubId)
                                .customTitle("fake objective title " + i)
                                .customDescription("fake objective description " + i)
                                .build()
                ).collect(Collectors.toList());
    }
}