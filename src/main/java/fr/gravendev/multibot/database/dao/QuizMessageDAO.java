package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.data.MessageData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QuizMessageDAO extends DAO<MessageData> {

    public QuizMessageDAO(Connection connection) {
        super(connection);
    }

    @Override
    public boolean save(MessageData data) {
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO quiz_messages VALUES(?, ?) ON DUPLICATE KEY UPDATE text = ?");
            preparedStatement.setString(1, data.id);
            preparedStatement.setString(2, data.message);
            preparedStatement.setString(3, data.message);

            preparedStatement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public MessageData get(String value) {
        MessageData messageData = null;

        try {
            PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM quiz_messages WHERE id = ?");
            statement.setString(1, value);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String id = resultSet.getString("id");
                String text = resultSet.getString("text");
                messageData = new MessageData(id, text);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messageData;
    }

}
