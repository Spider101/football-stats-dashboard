package com.footballstatsdashboard.db;

import com.footballstatsdashboard.api.model.MatchPerformance;

import java.util.List;
import java.util.UUID;

public interface IMatchPerformanceEntityDAO extends IEntityDAO<MatchPerformance> {
    List<MatchPerformance> getMatchPerformanceOfPlayerInCompetition(UUID playerId, UUID competitionId);
}