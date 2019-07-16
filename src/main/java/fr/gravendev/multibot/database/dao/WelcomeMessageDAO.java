package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.data.MessageData;

import java.sql.*;

public class WelcomeMessageDAO extends DAO<MessageData> {

    public WelcomeMessageDAO(Connection connection) {
        super(connection);
    }

    @Override
    public boolean save(MessageData obj) {
        return true;
    }

    @Override
    public MessageData get(String value) {

        MessageData messageData = null;

        try {
            PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM welcome_messages WHERE id = ?");
            statement.setInt(1, Integer.valueOf(value));

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                messageData = new MessageData(resultSet.getString("text"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messageData;
    }

    @Override
    public void delete(MessageData obj) {

    }

}
