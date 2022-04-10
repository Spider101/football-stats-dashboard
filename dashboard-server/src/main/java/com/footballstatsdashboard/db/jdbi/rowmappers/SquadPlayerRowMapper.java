package com.footballstatsdashboard.db.jdbi.rowmappers;

import com.footballstatsdashboard.api.model.club.ImmutableSquadPlayer;
import com.footballstatsdashboard.api.model.club.SquadPlayer;
import com.google.common.collect.ImmutableList;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SquadPlayerRowMapper implements RowMapper<SquadPlayer> {
    @Override
    public SquadPlayer map(ResultSet rs, StatementContext ctx) throws SQLException {
        return ImmutableSquadPlayer.builder()
                .playerId(UUID.fromString(rs.getString("id")))
                .name(rs.getString("name"))
                .country(rs.getString("country"))
                .countryFlag(rs.getString("countryFlag"))
                .currentAbility(rs.getInt("currentAbility"))
                .role(rs.getString("role"))
                // we are adding match ratings for recent form later through independent sql queries
                // since the property cannot be null, just passing an empty list to get around the not null validation
                .recentForm(ImmutableList.of())
                .build();
    }
}