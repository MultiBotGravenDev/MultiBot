package fr.gravendev.multibot.database.dao;


import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.data.RoleData;

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
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO roles VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE emote_id = ?");

        preparedStatement.setString(1, roleData.getRoleId());
        preparedStatement.setString(2, roleData.getEmoteId());
        preparedStatement.setString(3, roleData.getChannelId());
        preparedStatement.setString(4, roleData.getEmoteId());
        preparedStatement.execute();
        return true;
    }

    @Override
    protected RoleData get(String value, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM roles WHERE role_id = ? OR emote_id = ? OR channel_id LIKE ?");

        preparedStatement.setString(1, value);
        preparedStatement.setString(2, value);
        preparedStatement.setString(3, value);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            String roleId = resultSet.getString("role_id");
            String emoteID = resultSet.getString("emote_id");
            String channelId = resultSet.getString("channel_id");

            return new RoleData(roleId, emoteID, channelId);
        }
        return null;
    }

    @Override
    protected void delete(RoleData roleData, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM roles WHERE role_id = ?");

        preparedStatement.setString(1, String.valueOf(roleData.getRoleId()));
        preparedStatement.execute();
    }

}
