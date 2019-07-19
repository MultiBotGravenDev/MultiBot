package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.logs.MessageData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogsDAO extends DAO<MessageData>{

    public LogsDAO(Connection connection) {
        super(connection);
    }

    @Override
    public boolean save(MessageData message) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "INSERT INTO logs(`discord_id`, `message_id`, `content`, `creation`) VALUES (?, ?, ?, NOW())")){

            statement.setString(1, message.getDiscordID());
            statement.setString(2, message.getMessage_id());
            statement.setString(3, message.getContent());

            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
