package com.footballstatsdashboard.db;

import com.footballstatsdashboard.api.model.club.BoardObjective;

import java.util.List;
import java.util.UUID;

public interface IBoardObjectiveEntityDAO extends IEntityDAO<BoardObjective> {
    boolean doesEntityBelongToUser(UUID entityId, UUID userId);
    boolean doesEntityBelongToClub(UUID entityId, UUID clubId);
    List<BoardObjective> getBoardObjectivesForClub(UUID clubId);
}