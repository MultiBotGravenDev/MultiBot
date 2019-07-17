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
    public MessageData get(String value) {
        MessageData messageData = null;

        try {
            PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM quiz_messages WHERE id = ?");
            statement.setString(1, value);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                messageData = new MessageData(resultSet.getString("text"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messageData;
    }

}
