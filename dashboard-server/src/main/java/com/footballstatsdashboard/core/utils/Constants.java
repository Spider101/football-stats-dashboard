package com.footballstatsdashboard.core.utils;

public final class Constants {
    public Constants() {
        throw new AssertionError("Should not be initialized");
    }

    public static final String APPLICATION_NAME = "football-dashboard";
    public static final String PLAYER_V1_BASE_PATH = "football-stats-dashboard/v1/players";
    public static final String PLAYER_ID = "playerId";
    public static final String PLAYER_ID_PATH = "/{" + PLAYER_ID + "}";

}
