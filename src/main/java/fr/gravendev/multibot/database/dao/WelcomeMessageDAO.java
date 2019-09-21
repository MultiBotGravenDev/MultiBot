package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.data.MessageData;

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

        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO welcome_messages VALUES(?, ?) ON DUPLICATE KEY UPDATE text = ?");
        preparedStatement.setString(1, obj.getId());
        preparedStatement.setString(2, obj.getMessage());

        preparedStatement.setString(3, obj.getMessage());

        preparedStatement.execute();

        return true;
    }

    @Override
    protected MessageData get(String value, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM welcome_messages WHERE id = ?");
        statement.setInt(1, Integer.valueOf(value));

        ResultSet resultSet = statement.executeQuery();

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
