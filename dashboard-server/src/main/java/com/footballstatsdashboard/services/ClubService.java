package com.footballstatsdashboard.services;

import com.footballstatsdashboard.api.model.Club;
import com.footballstatsdashboard.api.model.ImmutableClub;
import com.footballstatsdashboard.api.model.club.ClubSummary;
import com.footballstatsdashboard.api.model.club.Expenditure;
import com.footballstatsdashboard.api.model.club.ImmutableExpenditure;
import com.footballstatsdashboard.api.model.club.ImmutableIncome;
import com.footballstatsdashboard.api.model.club.ImmutableManagerFunds;
import com.footballstatsdashboard.api.model.club.Income;
import com.footballstatsdashboard.api.model.club.ManagerFunds;
import com.footballstatsdashboard.api.model.club.SquadPlayer;
import com.footballstatsdashboard.db.ClubDAO;
import com.footballstatsdashboard.db.key.ResourceKey;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
        ManagerFunds newManagerFunds = ImmutableManagerFunds.builder()
                .from(incomingClub.getManagerFunds())
                .history(Collections.singletonList(incomingClub.getManagerFunds().getCurrent()))
                .build();

        Income newClubIncome = ImmutableIncome.builder()
                .from(Objects.requireNonNull(incomingClub.getIncome()))
                .history(Collections.singletonList(incomingClub.getIncome().getCurrent()))
                .build();

        Expenditure newClubExpenditure = ImmutableExpenditure.builder()
                .from(Objects.requireNonNull(incomingClub.getExpenditure()))
                .history(Collections.singletonList(incomingClub.getExpenditure().getCurrent()))
                .build();

        LocalDate currentDate = LocalDate.now();
        Club newClub = ImmutableClub.builder()
                .from(incomingClub)
                .userId(userId)
                .managerFunds(newManagerFunds)
                .income(newClubIncome)
                .expenditure(newClubExpenditure)
                .createdDate(currentDate)
                .lastModifiedDate(currentDate)
                .createdBy(createdBy)
                .build();

        ResourceKey resourceKey = new ResourceKey(newClub.getId());
        this.clubDAO.insertDocument(resourceKey, newClub);

        return newClub;
    }

    public Club updateClub(Club incomingClub, Club existingClub, UUID existingClubId) {
        ImmutableClub.Builder updatedClubBuilder = ImmutableClub.builder()
                .from(existingClub);

        // update manager funds entity only if the total budget no longer matches the existing manager fund value
        // if they are equal, it implies that only the transfer and wage budget split has changed and therefore, there
        // is no need to update the managerFunds entity
        BigDecimal updatedBudget = incomingClub.getTransferBudget().add(incomingClub.getWageBudget());
        if (existingClub.getManagerFunds().getCurrent().compareTo(updatedBudget) != 0) {
            ManagerFunds updatedManagerFunds = ImmutableManagerFunds.builder()
                    .from(existingClub.getManagerFunds())
                    .current(incomingClub.getManagerFunds().getCurrent())
                    .addHistory(incomingClub.getManagerFunds().getCurrent())
                    .build();
            updatedClubBuilder.managerFunds(updatedManagerFunds);
        }

        Club updatedClub = updatedClubBuilder
                .name(incomingClub.getName())
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