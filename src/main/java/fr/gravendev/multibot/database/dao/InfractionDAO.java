package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.moderation.InfractionType;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class InfractionDAO extends DAO<InfractionData> {

    public InfractionDAO(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    @Override
    protected boolean save(InfractionData data, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO infractions(`uuid`, `punished_id`, `punisher_id`, `type`, `reason`, `start`, `end`)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)" +
                        "ON DUPLICATE KEY UPDATE end = VALUES(end)");

        statement.setString(1, data.getUUID().toString());
        statement.setString(2, data.getPunished_id());
        statement.setString(3, data.getPunisher_id());
        statement.setString(4, data.getType().name());
        statement.setString(5, data.getReason());
        statement.setTimestamp(6, new Timestamp(data.getStart().getTime()));
        statement.setTimestamp(7, data.getEnd() != null ? new Timestamp(data.getEnd().getTime()) : null);

        statement.executeUpdate();

        return true;
    }

    @Override
    protected InfractionData get(String discordID, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM infractions WHERE punished_id = ? AND (END < NOW() OR END IS NULL) ORDER BY start DESC");
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
            return new InfractionData(uuid, punished_id, punisher_id, type, reason, start, end);
        }

        return null;
    }

    public List<InfractionData> getALLInfractions(String discordID) throws SQLException {
        Connection connection = getConnection();
        List<InfractionData> infractions = new ArrayList<>();

        PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM infractions WHERE punished_id = ? AND type = 'warn' ORDER BY start DESC");
        statement.setString(1, discordID);

        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            UUID uuid = UUID.fromString(resultSet.getString("uuid"));
            String punished_id = resultSet.getString("punished_id");
            String punisher_id = resultSet.getString("punisher_id");
            InfractionType type = InfractionType.valueOf(resultSet.getString("type"));
            String reason = resultSet.getString("reason");
            Date start = new Date(resultSet.getTimestamp("start").getTime());
            Date end = resultSet.getTimestamp("end") != null ?
                    new Date(resultSet.getTimestamp("end").getTime()) : null;

            infractions.add(new InfractionData(uuid, punished_id, punisher_id, type, reason, start, end));
        }

        return infractions;
    }

    @Override
    protected void delete(InfractionData obj, Connection connection) {
    }
}
