package com.footballstatsdashboard.db;

import com.footballstatsdashboard.api.model.club.BoardObjective;

import java.util.List;
import java.util.UUID;

public interface IBoardObjectiveEntityDAO extends IEntityDAO<BoardObjective> {
    List<BoardObjective> getBoardObjectivesForClub(UUID clubId);
}