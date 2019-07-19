package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.data.InfractionData;
import java.sql.*;

public class InfractionDAO extends DAO<InfractionData> {

    public InfractionDAO(Connection connection) {
        super(connection);
    }

    @Override
    public boolean save(InfractionData data) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "INSERT INTO infractions(`uuid`, `punished_id`, `punisher_id`, `type`, `reason`, `start`, `end`)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)" +
                        "ON DUPLICATE KEY UPDATE end = VALUES(end)")){

            statement.setString(1, data.getUUID().toString());
            statement.setString(2, data.getPunished_id());
            statement.setString(3, data.getPunisher_id());
            statement.setString(4, data.getType().name());
            statement.setString(5, data.getReason());
            statement.setTimestamp(6, new Timestamp(data.getStart().getTime()));
            statement.setTimestamp(7, new Timestamp(data.getEnd().getTime()));

            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public InfractionData get(String value) {
        return null;
    }
}
