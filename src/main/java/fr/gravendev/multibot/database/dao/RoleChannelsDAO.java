package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.data.RoleChannelData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleChannelsDAO extends DAO<RoleChannelData> {

    public RoleChannelsDAO(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    @Override
    protected boolean save(RoleChannelData roleChannelData, Connection connection) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO roles_channels VALUES(?, ?) ON DUPLICATE KEY UPDATE role_id = ?");

        preparedStatement.setString(1, roleChannelData.roleId);
        preparedStatement.setString(2, roleChannelData.channelsId);
        preparedStatement.setString(3, roleChannelData.roleId);
        preparedStatement.execute();

        return true;
    }

    @Override
    protected RoleChannelData get(String value, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM roles_channels WHERE channel_id = ?");

        preparedStatement.setString(1, value);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            String roleId = resultSet.getString("role_id");
            String channelsIds = resultSet.getString("channel_id");

            return new RoleChannelData(roleId, channelsIds);
        }

        return null;
    }

    @Override
    protected void delete(RoleChannelData roleChannelData, Connection connection) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM roles_channels WHERE channel_id = ?");

        preparedStatement.setString(1, String.valueOf(roleChannelData.channelsId));
        preparedStatement.execute();

    }

}
