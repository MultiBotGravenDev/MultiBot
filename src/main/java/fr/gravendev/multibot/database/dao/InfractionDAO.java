package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.data.ExperienceData;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.moderation.InfractionType;

import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
            statement.setTimestamp(7, data.getEnd() != null ? new Timestamp(data.getEnd().getTime()) : null);

            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public InfractionData get(String discordID) {
        InfractionData data = null;
        try (PreparedStatement statement = this.connection.prepareStatement(
                "SELECT * FROM infractions WHERE punished_id = ? AND (END < NOW() OR END IS NULL) ORDER BY start DESC")) {
            statement.setString(1, discordID);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                String punished_id = resultSet.getString("punished_id");
                String punisher_id = resultSet.getString("punisher_id");
                InfractionType type = InfractionType.valueOf(resultSet.getString("type"));
                String reason = resultSet.getString("reason");
                Date start = new Date(resultSet.getTimestamp("start").getTime());
                Date end = resultSet.getTimestamp("end") != null ?
                        new Date(resultSet.getTimestamp("end").getTime()) : null;
                data = new InfractionData(uuid, punished_id, punisher_id, type, reason, start, end);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
}
