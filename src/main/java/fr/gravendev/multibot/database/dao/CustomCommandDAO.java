package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.data.CustomCommandData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomCommandDAO extends DAO<CustomCommandData> {

    public CustomCommandDAO(Connection connection) {
        super(connection);
    }

    @Override
    public boolean save(CustomCommandData obj) {

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO custom_commands VALUES(?, ?) ON DUPLICATE KEY UPDATE text = ?");
            preparedStatement.setString(1, obj.command);
            preparedStatement.setString(2, obj.text);

            preparedStatement.setString(3, obj.text);

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public CustomCommandData get(String value) {

        CustomCommandData customCommandData = null;

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM custom_commands WHERE command = ?");
            preparedStatement.setString(1, value);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String command = resultSet.getString("command");
                String text = resultSet.getString("text");

                customCommandData = new CustomCommandData(command, text);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customCommandData;
    }

    @Override
    public void delete(CustomCommandData obj) {

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("DELETE FROM custom_commands WHERE command = ?");
            preparedStatement.setString(1, obj.command);

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
