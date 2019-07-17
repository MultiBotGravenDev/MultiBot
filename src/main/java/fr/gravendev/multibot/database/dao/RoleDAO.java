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
            preparedStatement.setString(1, obj.roleId);
            preparedStatement.setString(2, obj.emoteId);

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
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM roles WHERE role_id = ? OR emote_id = ?");
            preparedStatement.setString(1, value);
            preparedStatement.setString(2, value);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String roleId = resultSet.getString("role_id");
                String emoteID = resultSet.getString("emote_id");
                roleData = new RoleData(roleId, emoteID);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return roleData;
    }

    @Override
    public void delete(RoleData roleData) {

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("DELETE FROM roles WHERE role_id = ?");
            preparedStatement.setString(1, roleData.roleId + "");

            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
