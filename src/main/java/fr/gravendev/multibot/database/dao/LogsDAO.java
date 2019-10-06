package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.logs.MessageData;
import fr.gravendev.multibot.utils.PreparedStatementBuilder;

import java.sql.*;

public class LogsDAO extends DAO<MessageData> {
    public LogsDAO(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    @Override
    protected boolean save(MessageData message, Connection connection) throws SQLException {
        new PreparedStatementBuilder(connection)
                .prepareStatement("INSERT INTO logs(`discord_id`, `message_id`, `content`, `creation`) VALUES (?, ?, ?, NOW())")
                .setString(message.getDiscordID())
                .setString(message.getMessage_id())
                .setString(message.getContent())
                .executeUpdate();
        return true;
    }


    @Override
    protected MessageData get(String value, Connection connection) throws SQLException {
        ResultSet resultSet = new PreparedStatementBuilder(connection)
                .prepareStatement("SELECT * FROM logs WHERE message_id = ?")
                .setString(value)
                .executeQuery();

        if (resultSet.next()) {
            String discordId = resultSet.getString("discordId");
            String messageId = resultSet.getString("messageId");
            String content = resultSet.getString("content");
            Date creation = resultSet.getDate("creation");

            return new MessageData(discordId, messageId, content, creation.getTime());
        }
        return null;
    }

    @Override
    protected void delete(MessageData obj, Connection connection) {
    }
}
