package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.data.WelcomeMessageData;
import fr.gravendev.multibot.database.DAO;

import java.sql.*;

public class WelcomeMessageDAO extends DAO<WelcomeMessageData> {

    public WelcomeMessageDAO(Connection connection) {
        super(connection);
    }

    @Override
    public boolean save(WelcomeMessageData obj) {
        return true;
    }

    @Override
    public WelcomeMessageData get(String value) {

        WelcomeMessageData welcomeMessageData = null;

        try {
            PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM welcome_messages WHERE id = ?");
            statement.setInt(1, Integer.valueOf(value));

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                welcomeMessageData = new WelcomeMessageData(resultSet.getString("text"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return welcomeMessageData;
    }

    @Override
    public void delete(WelcomeMessageData obj) {

    }

}
