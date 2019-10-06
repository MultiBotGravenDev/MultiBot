package fr.gravendev.multibot.database.dao;


import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.data.RoleData;
import fr.gravendev.multibot.utils.PreparedStatementBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleDAO extends DAO<RoleData> {
    public RoleDAO(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    @Override
    protected boolean save(RoleData roleData, Connection connection) throws SQLException {
        new PreparedStatementBuilder(connection)
                .prepareStatement("INSERT INTO roles VALUES(?, ?) ON DUPLICATE KEY UPDATE emote_id = ?")
                .setString(roleData.getRoleId())
                .setString(roleData.getEmoteId())
                .setString(roleData.getEmoteId())
                .execute();
        return true;
    }

    @Override
    protected RoleData get(String value, Connection connection) throws SQLException {
        ResultSet resultSet = new PreparedStatementBuilder(connection)
                .prepareStatement("SELECT * FROM roles WHERE role_id = ? OR emote_id = ?")
                .setString(value)
                .setString(value)
                .executeQuery();

        if (resultSet.next()) {
            String roleId = resultSet.getString("role_id");
            String emoteID = resultSet.getString("emote_id");

            return new RoleData(roleId, emoteID);
        }
        return null;
    }

    @Override
    protected void delete(RoleData roleData, Connection connection) throws SQLException {
        new PreparedStatementBuilder(connection)
                .prepareStatement("DELETE FROM roles WHERE role_id = ?")
                .setString(String.valueOf(roleData.getRoleId()))
                .execute();
    }
}
