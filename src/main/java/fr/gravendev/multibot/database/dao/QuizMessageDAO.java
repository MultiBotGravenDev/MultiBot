package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.data.MessageData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QuizMessageDAO extends DAO<MessageData> {

    public QuizMessageDAO(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    @Override
    protected boolean save(MessageData data, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO quiz_messages VALUES(?, ?) ON DUPLICATE KEY UPDATE text = ?");
        preparedStatement.setString(1, data.getId());
        preparedStatement.setString(2, data.getMessage());
        preparedStatement.setString(3, data.getMessage());

        preparedStatement.executeUpdate();

        return true;

    }

    @Override
    protected MessageData get(String value, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM quiz_messages WHERE id = ?");
        statement.setString(1, value);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            String id = resultSet.getString("id");
            String text = resultSet.getString("text");
            return new MessageData(id, text);
        }
        return null;
    }

    @Override
    protected void delete(MessageData obj, Connection connection) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM quiz_messages WHERE id = ?");
        preparedStatement.setString(1, obj.getId());

        preparedStatement.execute();

    }

}

