package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.logs.MessageData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogsDAO extends DAO<MessageData> {

    public LogsDAO(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    @Override
    protected boolean save(MessageData message, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO logs(`discord_id`, `message_id`, `content`, `creation`) VALUES (?, ?, ?, NOW())");

        statement.setString(1, message.getDiscordID());
        statement.setString(2, message.getMessage_id());
        statement.setString(3, message.getContent());

        statement.executeUpdate();

        return true;
    }


    @Override
    protected MessageData get(String value, Connection connection) {
        return null;
    }

    @Override
    protected void delete(MessageData obj, Connection connection) {
    }
}
