package com.footballstatsdashboard.core.utils;

import org.apache.commons.lang3.tuple.Pair;
import java.util.Map;

public final class Constants {
    private Constants() {
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

    public static final Map<String, Pair<String, String>> PLAYER_ATTRIBUTE_CATEGORY_MAP = Map.ofEntries(
            Map.entry("freekick accuracy", Pair.of("Technical", "Attacking")),
            Map.entry("penalties", Pair.of("Technical", "Attacking")),
            Map.entry("crossing", Pair.of("Technical", "Attacking")),
            Map.entry("long shots", Pair.of("Technical", "Attacking")),
            Map.entry("finishing", Pair.of("Technical", "Attacking")),
            Map.entry("volleys", Pair.of("Technical", "Attacking")),
            Map.entry("ball control", Pair.of("Technical", "Attacking")),
            Map.entry("dribbling", Pair.of("Technical", "Attacking")),
            Map.entry("curve", Pair.of("Technical", "Attacking")),
            Map.entry("short passing", Pair.of("Technical", "Vision")),
            Map.entry("long Passing", Pair.of("Technical", "Vision")),
            Map.entry("standing tackle", Pair.of("Technical", "Defending")),
            Map.entry("sliding tackle", Pair.of("Technical", "Defending")),
            Map.entry("heading accuracy", Pair.of("Technical", "Aerial")),
            Map.entry("jumping", Pair.of("Physical", "Aerial")),
            Map.entry("stamina", Pair.of("Physical", "Defending")),
            Map.entry("strength", Pair.of("Physical", "Defending")),
            Map.entry("sprint speed", Pair.of("Physical", "Speed")),
            Map.entry("acceleration", Pair.of("Physical", "Speed")),
            Map.entry("agility", Pair.of("Physical", "Speed")),
            Map.entry("balance", Pair.of("Physical", "Speed")),
            Map.entry("aggression", Pair.of("Mental", "Attacking")),
            Map.entry("composure", Pair.of("Mental", "Attacking")),
            Map.entry("attacking position", Pair.of("Mental", "Attacking")),
            Map.entry("defensive awareness", Pair.of("Mental", "Defending")),
            Map.entry("vision", Pair.of("Mental", "Vision"))
    );
}