package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.data.AntiRoleData;
import fr.gravendev.multibot.utils.PreparedStatementBuilder;

import java.sql.Connection;
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
            String userId = String.valueOf(antiRoleData.getUserId());
            String roleName = entry.getValue();
            new PreparedStatementBuilder(connection)
                    .prepareStatement("INSERT IGNORE INTO anti_roles VALUES(?, ?, NOW())")
                    .setString(userId)
                    .setString(roleName)
                    .execute();
        }
        return true;
    }

    // TODO Refactor
    @Override
    public AntiRoleData get(String value, Connection connection) throws SQLException {
        ResultSet resultSet = new PreparedStatementBuilder(connection)
                .prepareStatement("SELECT * FROM anti_roles WHERE user_id = ?")
                .setString(value)
                .executeQuery();
        Map<Date, String> roles = new HashMap<>();
        long userId = 0L;

        while (resultSet.next()) {
            userId = Long.parseLong(resultSet.getString("user_id"));
            Timestamp start = resultSet.getTimestamp("start");
            String role = resultSet.getString("role");

            roles.put(start, role);
        }

        if (userId != 0L) {
            return new AntiRoleData(userId, roles);
        }
        return new AntiRoleData(Long.parseLong(value), new HashMap<>());
    }

    // TODO Refactor it, it has sort of duplicated code
    @Override
    public void delete(AntiRoleData antiRoleData, Connection connection) throws SQLException {
        for (Map.Entry<Date, String> entry : antiRoleData.getRoles().entrySet()) {
            Date roleDate = entry.getKey();
            Calendar calendar = Calendar.getInstance();

            calendar.setTime(roleDate);
            calendar.add(Calendar.MONTH, 1);

            boolean hasExpired = new Date().after(calendar.getTime());

            if (hasExpired) {
                String userId = String.valueOf(antiRoleData.getUserId());
                String roleName = entry.getValue();
                new PreparedStatementBuilder(connection)
                        .prepareStatement("DELETE FROM anti_roles WHERE user_id = ? AND role = ?")
                        .setString(userId)
                        .setString(roleName)
                        .execute();
            }
        }
    }
}
