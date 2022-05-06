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
import com.footballstatsdashboard.core.exceptions.ServiceException;
import com.footballstatsdashboard.core.validations.Validation;
import com.footballstatsdashboard.core.validations.ValidationSeverity;
import com.footballstatsdashboard.db.IClubEntityDAO;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ClubService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClubService.class);
    private final IClubEntityDAO clubDAO;

    public ClubService(IClubEntityDAO clubDAO) {
        this.clubDAO = clubDAO;
    }

    // TODO: 1/22/2022 add some tests for this
    public boolean doesClubBelongToUser(UUID clubId, UUID authorizedUserId) {
        Club club = fetchClubData(clubId);
        return authorizedUserId.equals(club.getUserId());
    }

    public Club getClub(UUID clubId, UUID authorizedUserId) {
        Club club = fetchClubData(clubId);

        // validate that the user has access to the club data being fetched
        if (!authorizedUserId.equals(club.getUserId())) {
            LOGGER.error("Club with ID: {} does not belong to user making request (ID: {})",
                    clubId, authorizedUserId);
            throw new ServiceException(HttpStatus.FORBIDDEN_403, "User does not have access to this club!");
        }

        return club;
    }

    public Club createClub(Club incomingClub, UUID userId, String createdBy) {
        List<Validation> validationList = validateIncomingClub(incomingClub, true);
        if (!validationList.isEmpty()) {
            LOGGER.error("Unable to create new club! Found errors: {}", validationList);
            throw new ServiceException(HttpStatus.UNPROCESSABLE_ENTITY_422, "Invalid incoming club data.",
                    validationList);
        }

        // process and persist the club data if no validations found
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

        this.clubDAO.insertEntity(newClub);

        return newClub;
    }

    public Club updateClub(Club incomingClub, UUID existingClubId, UUID authorizedUserId) {
        Club existingClub = getClub(existingClubId, authorizedUserId);

        List<Validation> validationList = validateIncomingClub(incomingClub, false);
        if (!validationList.isEmpty()) {
            LOGGER.error("Unable to update club! Found errors: {}", validationList);
            throw new ServiceException(HttpStatus.UNPROCESSABLE_ENTITY_422, "Invalid incoming club entity.",
                    validationList);
        }

        // process and update the club data if no validations are found
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

        this.clubDAO.updateEntity(existingClubId, updatedClub);

        return updatedClub;
    }

    public void deleteClub(UUID clubId, UUID authorizedUserId) {
        try {
            // ensure user has access to the club that is being requested to be deleted
            if (!this.clubDAO.doesEntityBelongToUser(clubId, authorizedUserId)) {
                LOGGER.error("Club with ID: {} does not belong to user making request (ID: {})",
                    clubId, authorizedUserId);
                throw new ServiceException(HttpStatus.FORBIDDEN_403, "User does not have access to this club!");
            }

            this.clubDAO.deleteEntity(clubId);
        } catch (EntityNotFoundException | NoResultException persistenceException) {
            LOGGER.error("Cannot delete club with ID: {} that does not exist", clubId);
            throw new ServiceException(HttpStatus.NOT_FOUND_404,
                    String.format("No club entity found for ID: %s", clubId));
        }
    }

    public List<ClubSummary> getClubSummariesByUserId(UUID userId) {
        return this.clubDAO.getClubSummariesForUser(userId);
    }

    public List<SquadPlayer> getSquadPlayers(UUID clubId) {
        return this.clubDAO.getPlayersInClub(clubId);
    }

    private Club fetchClubData(UUID clubId) {
        try {
            return this.clubDAO.getEntity(clubId);
        } catch (EntityNotFoundException entityNotFoundException) {
            String errorMessage = String.format("No club entity found for ID: %s", clubId);
            LOGGER.error(errorMessage);
            throw new ServiceException(HttpStatus.NOT_FOUND_404, errorMessage);
        }
    }

    private List<Validation> validateIncomingClub(Club incomingClub, boolean isForNewClub) {
        List<Validation> validationList = new ArrayList<>();
        if (isForNewClub) {
            if (StringUtils.isEmpty(incomingClub.getName())) {
                validationList.add(new Validation(ValidationSeverity.ERROR, "Empty club name is not allowed!"));
            }

            if (incomingClub.getIncome() == null) {
                validationList.add(new Validation(ValidationSeverity.ERROR, "New club must have income data!"));
            }

            if (incomingClub.getExpenditure() == null) {
                validationList.add(new Validation(ValidationSeverity.ERROR, "New club must have expenditure data!"));
            }
        }

        BigDecimal totalBudget = incomingClub.getTransferBudget().add(incomingClub.getWageBudget());
        if (totalBudget.compareTo(incomingClub.getManagerFunds().getCurrent()) != 0) {
            validationList.add(new Validation(ValidationSeverity.ERROR,
                    "Transfer and wage budget must add up to manager funds!"));
        }

        return validationList;
    }
}