package com.footballstatsdashboard.core.utils;

import com.footballstatsdashboard.api.model.player.AttributeCategory;
import com.footballstatsdashboard.api.model.player.AttributeGroup;
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

    public static final String COUNTRY_FLAG_LOOKUP_V1_BASE_PATH = "football-stats-dashboard/v1/lookup/countryFlags";
    public static final String FILE_UPLOAD_V1_BASE_PATH = "football-stats-dashboard/v1/upload";

    public static final int HASHING_COST = 12;
    public static final String COUNTRY_CODE_MAPPING_FNAME = "countryCodeMapping.json";
    public static final String COUNTRY_FLAG_URL_TEMPLATE = "https://flagcdn.com/w40/%s.png";

    public static final Map<String, Pair<AttributeCategory, AttributeGroup>> PLAYER_ATTRIBUTE_CATEGORY_MAP =
            Map.ofEntries(
                    Map.entry("freekick accuracy", Pair.of(AttributeCategory.TECHNICAL, AttributeGroup.ATTACKING)),
                    Map.entry("penalties", Pair.of(AttributeCategory.TECHNICAL, AttributeGroup.ATTACKING)),
                    Map.entry("crossing", Pair.of(AttributeCategory.TECHNICAL, AttributeGroup.ATTACKING)),
                    Map.entry("long shots", Pair.of(AttributeCategory.TECHNICAL, AttributeGroup.ATTACKING)),
                    Map.entry("finishing", Pair.of(AttributeCategory.TECHNICAL, AttributeGroup.ATTACKING)),
                    Map.entry("volleys", Pair.of(AttributeCategory.TECHNICAL, AttributeGroup.ATTACKING)),
                    Map.entry("ball control", Pair.of(AttributeCategory.TECHNICAL, AttributeGroup.ATTACKING)),
                    Map.entry("dribbling", Pair.of(AttributeCategory.TECHNICAL, AttributeGroup.ATTACKING)),
                    Map.entry("curve", Pair.of(AttributeCategory.TECHNICAL, AttributeGroup.ATTACKING)),
                    Map.entry("short passing", Pair.of(AttributeCategory.TECHNICAL, AttributeGroup.VISION)),
                    Map.entry("long passing", Pair.of(AttributeCategory.TECHNICAL, AttributeGroup.VISION)),
                    Map.entry("standing tackle", Pair.of(AttributeCategory.TECHNICAL, AttributeGroup.DEFENDING)),
                    Map.entry("sliding tackle", Pair.of(AttributeCategory.TECHNICAL, AttributeGroup.DEFENDING)),
                    Map.entry("heading accuracy", Pair.of(AttributeCategory.TECHNICAL, AttributeGroup.AERIAL)),
                    Map.entry("jumping", Pair.of(AttributeCategory.PHYSICAL, AttributeGroup.AERIAL)),
                    Map.entry("stamina", Pair.of(AttributeCategory.PHYSICAL, AttributeGroup.DEFENDING)),
                    Map.entry("strength", Pair.of(AttributeCategory.PHYSICAL, AttributeGroup.DEFENDING)),
                    Map.entry("sprint speed", Pair.of(AttributeCategory.PHYSICAL, AttributeGroup.SPEED)),
                    Map.entry("acceleration", Pair.of(AttributeCategory.PHYSICAL, AttributeGroup.SPEED)),
                    Map.entry("agility", Pair.of(AttributeCategory.PHYSICAL, AttributeGroup.SPEED)),
                    Map.entry("balance", Pair.of(AttributeCategory.PHYSICAL, AttributeGroup.SPEED)),
                    Map.entry("aggression", Pair.of(AttributeCategory.MENTAL, AttributeGroup.ATTACKING)),
                    Map.entry("composure", Pair.of(AttributeCategory.MENTAL, AttributeGroup.ATTACKING)),
                    Map.entry("attacking position", Pair.of(AttributeCategory.MENTAL, AttributeGroup.ATTACKING)),
                    Map.entry("defensive awareness", Pair.of(AttributeCategory.MENTAL, AttributeGroup.DEFENDING)),
                    Map.entry("vision", Pair.of(AttributeCategory.MENTAL, AttributeGroup.VISION))
            );
}