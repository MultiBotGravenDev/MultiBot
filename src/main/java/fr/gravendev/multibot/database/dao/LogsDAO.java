package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.logs.MessageData;

import java.sql.*;

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
    protected MessageData get(String value, Connection connection) throws SQLException {
        MessageData messageData = null;

        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM logs WHERE message_id = ?");
        preparedStatement.setString(1, value);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {

            String discord_id = resultSet.getString("discord_id");
            String message_id = resultSet.getString("message_id");
            String content = resultSet.getString("content");
            Date creation = resultSet.getDate("creation");
            messageData = new MessageData(discord_id, message_id, content, creation.getTime());

        }

        return messageData;
    }

    @Override
    protected void delete(MessageData obj, Connection connection) {
    }
}
