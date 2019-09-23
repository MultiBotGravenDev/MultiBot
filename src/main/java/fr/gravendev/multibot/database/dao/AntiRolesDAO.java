package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.data.AntiRoleData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AntiRolesDAO extends DAO<AntiRoleData> {
    public AntiRolesDAO(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    @Override
    public boolean save(AntiRoleData antiRoleData, Connection connection) throws SQLException {
        for (Map.Entry<Date, String> entry : antiRoleData.getRoles().entrySet()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT IGNORE INTO anti_roles VALUES(?, ?, NOW())");

            preparedStatement.setString(1, String.valueOf(antiRoleData.getUserId()));
            preparedStatement.setString(2, entry.getValue());
            preparedStatement.execute();
        }
        return true;
    }

    @Override
    public AntiRoleData get(String value, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM anti_roles WHERE user_id = ?");

        preparedStatement.setString(1, value);

        ResultSet resultSet = preparedStatement.executeQuery();
        Map<Date, String> roles = new HashMap<>();
        long userId = 0L;

        while (resultSet.next()) {
            userId = Long.parseLong(resultSet.getString("user_id"));
            Timestamp start = resultSet.getTimestamp("start");
            String role = resultSet.getString("role");

            roles.put(start, role);
        }
        return userId != 0L ? new AntiRoleData(userId, roles) : new AntiRoleData(Long.parseLong(value), new HashMap<>());
    }

    @Override
    public void delete(AntiRoleData obj, Connection connection) throws SQLException {
        for (Map.Entry<Date, String> entry : obj.getRoles().entrySet()) {
            Calendar calendar = Calendar.getInstance();

            calendar.setTime(entry.getKey());
            calendar.add(Calendar.MONTH, 1);

            if (new Date().after(calendar.getTime())) {
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM anti_roles WHERE user_id = ? AND role = ?");

                preparedStatement.setString(1, String.valueOf(obj.getUserId()));
                preparedStatement.setString(2, entry.getValue());
                preparedStatement.execute();
            }
        }
    }
}
