package com.footballstatsdashboard.db;

import com.footballstatsdashboard.api.model.Player;

import java.util.UUID;

public interface IPlayerEntityDAO extends IEntityDAO<Player> {
    boolean doesEntityExist(UUID entityId);
    boolean doesEntityBelongToUser(UUID entityId, UUID userId);
}