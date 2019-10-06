package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.data.MessageData;
import fr.gravendev.multibot.utils.PreparedStatementBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WelcomeMessageDAO extends DAO<MessageData> {
    public WelcomeMessageDAO(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    @Override
    protected boolean save(MessageData obj, Connection connection) throws SQLException {
        new PreparedStatementBuilder(connection)
                .prepareStatement("INSERT INTO welcome_messages VALUES(?, ?) ON DUPLICATE KEY UPDATE text = ?")
                .setString(obj.getId())
                .setString(obj.getMessage())
                .setString(obj.getMessage())
                .execute();
        return true;
    }

    @Override
    protected MessageData get(String value, Connection connection) throws SQLException {
        ResultSet resultSet = new PreparedStatementBuilder(connection)
                .prepareStatement("SELECT * FROM welcome_messages WHERE id = ?")
                .setInt(Integer.valueOf(value))
                .executeQuery();

        if (resultSet.next()) {
            String id = resultSet.getString("id");
            String text = resultSet.getString("text");

            return new MessageData(id, text);
        }
        return null;
    }

    @Override
    protected void delete(MessageData obj, Connection connection) {
    }
}
