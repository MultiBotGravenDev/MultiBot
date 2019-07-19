package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.data.AntiRoleData;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AntiRolesDAO extends DAO<AntiRoleData> {

    public AntiRolesDAO(Connection connection) {
        super(connection);
    }

    @Override
    public boolean save(AntiRoleData obj) {

        try {

            for (Map.Entry<Date, String> entry : obj.roles.entrySet()) {


                PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT IGNORE INTO anti_roles VALUES(?, ?, ?)");
                preparedStatement.setString(1, String.valueOf(obj.userId));
                preparedStatement.setString(2, entry.getValue());
                preparedStatement.setDate(3, entry.getKey());

                preparedStatement.execute();

            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public AntiRoleData get(String value) {
        AntiRoleData antiRoleData = null;

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM anti_roles WHERE user_id = ?");
            preparedStatement.setString(1, value);

            ResultSet resultSet = preparedStatement.executeQuery();

            Map<Date, String> roles = new HashMap<>();
            long userId = 0L;

            while (resultSet.next()) {
                roles.put(resultSet.getDate("start"), resultSet.getString("role"));
                userId = Long.parseLong(resultSet.getString("user_id"));
            }

            antiRoleData = userId != 0L ? new AntiRoleData(userId, roles) : null;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return antiRoleData;
    }

    @Override
    public void delete(AntiRoleData obj) {

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("DELETE FROM anti_roles WHERE user_id = ?");
            preparedStatement.setString(1, String.valueOf(obj.userId));

            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
