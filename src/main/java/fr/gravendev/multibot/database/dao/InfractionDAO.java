package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.moderation.InfractionType;
import fr.gravendev.multibot.utils.PreparedStatementBuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

// TODO Remove all duplicated code
public class InfractionDAO extends DAO<InfractionData> {
    public InfractionDAO(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    @Override
    protected boolean save(InfractionData data, Connection connection) throws SQLException {
        new PreparedStatementBuilder(connection)
                .prepareStatement("INSERT INTO infractions(`uuid`, `punished_id`, `punisher_id`, `type`, `reason`, `start`, `end`, `finished`)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)" +
                        "ON DUPLICATE KEY UPDATE end = VALUES(end), finished = VALUES(finished)")
                .setString(data.getUUID().toString())
                .setString(data.getPunishedId())
                .setString(data.getPunisherId())
                .setString(data.getType().name())
                .setString(data.getReason())
                .setTimestamp(new Timestamp(data.getStart().getTime()))
                .setTimestamp(data.getEnd() != null ? new Timestamp(data.getEnd().getTime()) : null)
                .setBoolean(data.isFinished())
                .executeUpdate();
        return true;
    }

    @Override
    protected InfractionData get(String discordID, Connection connection) throws SQLException {
        ResultSet resultSet = new PreparedStatementBuilder(connection)
                .prepareStatement("SELECT * FROM infractions WHERE punished_id = ? AND (END < NOW() OR END IS NULL) ORDER BY start DESC")
                .setString(discordID)
                .executeQuery();

        if (resultSet.next()) {
            UUID uuid = UUID.fromString(resultSet.getString("uuid"));
            String punished_id = resultSet.getString("punished_id");
            String punisher_id = resultSet.getString("punisher_id");
            InfractionType type = InfractionType.valueOf(resultSet.getString("type"));
            String reason = resultSet.getString("reason");
            Date start = new Date(resultSet.getTimestamp("start").getTime());
            Date end = resultSet.getTimestamp("end") != null ?
                    new Date(resultSet.getTimestamp("end").getTime()) : null;
            boolean finished = resultSet.getBoolean("finished");

            return new InfractionData(uuid, punished_id, punisher_id, type, reason, start, end, finished);
        }
        return null;
    }

    public List<InfractionData> getAllInfractions(String discordID) throws SQLException {
        try (Connection connection = getConnection()) {
            List<InfractionData> infractions = new ArrayList<>();
            ResultSet resultSet = new PreparedStatementBuilder(connection)
                    .prepareStatement("SELECT * FROM infractions WHERE punished_id = ? AND type = 'warn' ORDER BY start DESC")
                    .setString(discordID)
                    .executeQuery();

            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                String punished_id = resultSet.getString("punished_id");
                String punisher_id = resultSet.getString("punisher_id");
                InfractionType type = InfractionType.valueOf(resultSet.getString("type"));
                String reason = resultSet.getString("reason");
                Date start = new Date(resultSet.getTimestamp("start").getTime());
                Date end = resultSet.getTimestamp("end") != null ?
                        new Date(resultSet.getTimestamp("end").getTime()) : null;
                boolean finished = resultSet.getBoolean("finished");

                infractions.add(new InfractionData(uuid, punished_id, punisher_id, type, reason, start, end, finished));
            }
            return infractions;
        }
    }

    public List<InfractionData> getAll(String discordID) throws SQLException {
        try (Connection connection = getConnection()) {
            List<InfractionData> infractions = new ArrayList<>();
            ResultSet resultSet = new PreparedStatementBuilder(connection)
                    .prepareStatement("SELECT * FROM infractions WHERE punished_id = ? ORDER BY start DESC")
                    .setString(discordID)
                    .executeQuery();

            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                String punished_id = resultSet.getString("punished_id");
                String punisher_id = resultSet.getString("punisher_id");
                InfractionType type = InfractionType.valueOf(resultSet.getString("type"));
                String reason = resultSet.getString("reason");
                Date start = new Date(resultSet.getTimestamp("start").getTime());
                Date end = resultSet.getTimestamp("end") != null ?
                        new Date(resultSet.getTimestamp("end").getTime()) : null;
                boolean finished = resultSet.getBoolean("finished");

                infractions.add(new InfractionData(uuid, punished_id, punisher_id, type, reason, start, end, finished));
            }
            return infractions;
        }
    }

    public List<InfractionData> getAllFinished() throws SQLException {
        try (Connection connection = getConnection()) {
            List<InfractionData> infractions = new ArrayList<>();
            ResultSet resultSet = new PreparedStatementBuilder(connection)
                    .prepareStatement("SELECT * FROM infractions WHERE END < NOW() AND FINISHED = 0")
                    .executeQuery();

            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                String punished_id = resultSet.getString("punished_id");
                String punisher_id = resultSet.getString("punisher_id");
                InfractionType type = InfractionType.valueOf(resultSet.getString("type"));
                String reason = resultSet.getString("reason");
                Date start = new Date(resultSet.getTimestamp("start").getTime());
                Date end = resultSet.getTimestamp("end") != null ?
                        new Date(resultSet.getTimestamp("end").getTime()) : null;
                boolean finished = resultSet.getBoolean("finished");

                infractions.add(new InfractionData(uuid, punished_id, punisher_id, type, reason, start, end, finished));
            }
            return infractions;
        }
    }

    public InfractionData getLast(String discordID, InfractionType infractionType) throws SQLException {
        try (Connection connection = getConnection()) {
            ResultSet resultSet = new PreparedStatementBuilder(connection)
                    .prepareStatement("SELECT * FROM infractions WHERE punished_id = ? AND type = ? ORDER BY start DESC")
                    .setString(discordID)
                    .setString(infractionType.name())
                    .executeQuery();

            if (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                String punished_id = resultSet.getString("punished_id");
                String punisher_id = resultSet.getString("punisher_id");
                InfractionType type = InfractionType.valueOf(resultSet.getString("type"));
                String reason = resultSet.getString("reason");
                Date start = new Date(resultSet.getTimestamp("start").getTime());
                Date end = resultSet.getTimestamp("end") != null ?
                        new Date(resultSet.getTimestamp("end").getTime()) : null;
                boolean finished = resultSet.getBoolean("finished");

                return new InfractionData(uuid, punished_id, punisher_id, type, reason, start, end, finished);
            }
            return null;
        }
    }

    @Override
    protected void delete(InfractionData obj, Connection connection) {
    }
}
