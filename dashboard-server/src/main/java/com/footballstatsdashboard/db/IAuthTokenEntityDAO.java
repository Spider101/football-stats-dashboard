package com.footballstatsdashboard.db;

import com.footballstatsdashboard.api.model.AuthToken;

import java.util.Optional;
import java.util.UUID;

public interface IAuthTokenEntityDAO extends IEntityDAO<AuthToken> {
    Optional<AuthToken> getAuthTokenForUser(UUID userId);
}