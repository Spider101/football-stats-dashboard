package com.footballstatsdashboard.core.utils;

public final class Constants {
    public Constants() {
        throw new AssertionError("Should not be initialized");
    }

    public static final String APPLICATION_NAME = "football-dashboard";

    public static final String PLAYER_V1_BASE_PATH = "football-stats-dashboard/v1/players";
    public static final String PLAYER_ID = "playerId";
    public static final String PLAYER_ID_PATH = "/{" + PLAYER_ID + "}";

    public static final String USER_V1_BASE_PATH = "football-stats-dashboard/v1/users";
    public static final String USER_ID = "userId";
    public static final String USER_ID_PATH = "/{" + USER_ID + "}";

    public static final String CLUB_V1_BASE_PATH = "football-stats-dashboard/v1/club";
    public static final String CLUB_ID = "clubId";
    public static final String CLUB_ID_PATH = "/{" + CLUB_ID + "}";

    public static final String MATCH_PERFORMANCE_V1_BASE_PATH = "football-stats-dashboard/v1/match-performance";
    public static final String MATCH_PERFORMANCE_ID = "matchPerformanceId";
    public static final String MATCH_PERFORMANCE_ID_PATH = "/{" + MATCH_PERFORMANCE_ID + "}";
    public static final String MATCH_PERFORMANCE_LOOKUP_PATH = "/lookup" + PLAYER_ID_PATH;

    public static final int HASHING_COST = 12;
}