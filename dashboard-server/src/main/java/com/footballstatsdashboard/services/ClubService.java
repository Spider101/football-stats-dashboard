package com.footballstatsdashboard.services;

import com.footballstatsdashboard.api.model.Club;
import com.footballstatsdashboard.api.model.club.ClubSummary;
import com.footballstatsdashboard.api.model.ImmutableClub;
import com.footballstatsdashboard.api.model.club.SquadPlayer;
import com.footballstatsdashboard.db.ClubDAO;
import com.footballstatsdashboard.db.key.ResourceKey;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ClubService {
    private final ClubDAO<ResourceKey> clubDAO;

    public ClubService(ClubDAO<ResourceKey> clubDAO) {
        this.clubDAO = clubDAO;
    }

    public Club getClub(UUID clubId) {
        ResourceKey resourceKey = new ResourceKey(clubId);
        return this.clubDAO.getDocument(resourceKey, Club.class);
    }

    public Club createClub(Club incomingClub, UUID userId, String createdBy) {
        LocalDate currentDate = LocalDate.now();
        Club newClub = ImmutableClub.builder()
                .from(incomingClub)
                .userId(userId)
                .createdDate(currentDate)
                .lastModifiedDate(currentDate)
                .createdBy(createdBy)
                .build();

        ResourceKey resourceKey = new ResourceKey(newClub.getId());
        this.clubDAO.insertDocument(resourceKey, newClub);

        return newClub;
    }

    public Club updateClub(Club incomingClub, Club existingClub, UUID existingClubId) {
        Club updatedClub = ImmutableClub.builder()
                .from(existingClub)
                .name(incomingClub.getName())
                .income(incomingClub.getIncome())
                .expenditure(incomingClub.getExpenditure())
                .transferBudget(incomingClub.getTransferBudget())
                .wageBudget(incomingClub.getWageBudget())
                .lastModifiedDate(LocalDate.now())
                .build();

        ResourceKey resourceKey = new ResourceKey(existingClubId);
        this.clubDAO.updateDocument(resourceKey, updatedClub);

        return updatedClub;
    }

    public void deleteClub(UUID clubId) {
        ResourceKey resourceKey = new ResourceKey(clubId);
        this.clubDAO.deleteDocument(resourceKey);
    }

    public List<ClubSummary> getClubSummariesByUserId(UUID userId) {
        return this.clubDAO.getClubSummariesByUserId(userId);
    }

    public List<SquadPlayer> getSquadPlayers(UUID clubId) {
        return this.clubDAO.getPlayersInClub(clubId);
    }
}