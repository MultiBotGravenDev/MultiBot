package fr.gravendev.multibot.database.dao;


import fr.gravendev.multibot.database.data.RoleData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleDAO extends DAO<RoleData> {

    public RoleDAO(Connection connection) {
        super(connection);
    }

    @Override
    public boolean save(RoleData obj) {

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO roles VALUES(?, ?)");
            preparedStatement.setLong(1, obj.roleId);
            preparedStatement.setLong(2, obj.emoteId);

            preparedStatement.execute();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public RoleData get(String value) {

        RoleData roleData = null;

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM roles WHERE role_id = ?");
            preparedStatement.setLong(1, Long.valueOf(value));

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                long roleId = resultSet.getLong("role_id");
                long emoteID = resultSet.getLong("emote_id");
                roleData = new RoleData(roleId, emoteID);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return roleData;
    }

}
