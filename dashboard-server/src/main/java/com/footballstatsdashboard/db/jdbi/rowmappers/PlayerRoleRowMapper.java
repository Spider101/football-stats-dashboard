package com.footballstatsdashboard.db.jdbi.rowmappers;

import com.footballstatsdashboard.api.model.player.ImmutableRole;
import com.footballstatsdashboard.api.model.player.Role;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PlayerRoleRowMapper implements RowMapper<Role> {
    @Override
    public Role map(ResultSet rs, StatementContext ctx) throws SQLException {
        List<String> associatedAttributes = List.of(rs.getString("associatedAttributes").split(","));
        return ImmutableRole.builder()
                .name(rs.getString("name"))
                .associatedAttributes(associatedAttributes)
                .build();
    }
}