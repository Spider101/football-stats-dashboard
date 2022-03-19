package com.footballstatsdashboard.db;

import com.footballstatsdashboard.api.model.Club;
import com.footballstatsdashboard.api.model.club.ClubSummary;
import com.footballstatsdashboard.api.model.club.SquadPlayer;

import java.util.List;
import java.util.UUID;

public interface IClubEntityDAO extends IEntityDAO<Club> {
    List<ClubSummary> getClubSummariesForUser(UUID userId);
    List<SquadPlayer> getPlayersInClub(UUID clubId);
}