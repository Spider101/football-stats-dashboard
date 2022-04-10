package com.footballstatsdashboard.db.jdbi.rowmappers;

import com.footballstatsdashboard.api.model.ImmutablePlayer;
import com.footballstatsdashboard.api.model.Player;
import com.footballstatsdashboard.api.model.player.ImmutableMetadata;
import com.footballstatsdashboard.api.model.player.Metadata;
import com.google.common.collect.ImmutableList;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerRowMapper implements RowMapper<Player> {
    @Override
    public Player map(ResultSet rs, StatementContext ctx) throws SQLException {
        Metadata metadata = ImmutableMetadata.builder()
                .age(rs.getInt("age"))
                .name(rs.getString("name"))
                .club(rs.getString("club"))
                .country(rs.getString("country"))
                .photo(rs.getString("photo"))
                .countryLogo(rs.getString("countryLogo"))
                .clubLogo(rs.getString("clubLogo"))
                .build();
        return ImmutablePlayer.builder()
                .id(UUID.fromString(rs.getString("id")))
                .metadata(metadata)
                // we are adding roles and attributes later through independent sql queries
                // since they cannot be null, just passing empty lists to get around the not null validation
                .roles(ImmutableList.of())
                .attributes(ImmutableList.of())
                .clubId(UUID.fromString(rs.getString("clubId")))
                .createdDate(rs.getDate("createdDate").toLocalDate())
                .lastModifiedDate(rs.getDate("lastModifiedDate").toLocalDate())
                .createdBy(rs.getString("createdBy"))
                .build();
    }
}