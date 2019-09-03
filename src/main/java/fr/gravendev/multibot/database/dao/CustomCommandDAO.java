package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.data.CustomCommandData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomCommandDAO extends DAO<CustomCommandData> {

    public CustomCommandDAO(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    @Override
    public boolean save(CustomCommandData obj, Connection connection) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO custom_commands(command, text) VALUES(?, ?) ON DUPLICATE KEY UPDATE text = ?");
        preparedStatement.setString(1, obj.getCommand());
        preparedStatement.setString(2, obj.getText());

        preparedStatement.setString(3, obj.getText());

        preparedStatement.execute();
        return true;
    }

    @Override
    public CustomCommandData get(String value, Connection connection) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM custom_commands WHERE command = ? OR id = ?");
        preparedStatement.setString(1, value);
        preparedStatement.setString(2, value);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            String command = resultSet.getString("command");
            String text = resultSet.getString("text");

            return new CustomCommandData(command, text);
        }
        return null;
    }

    @Override
    public void delete(CustomCommandData obj, Connection connection) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM custom_commands WHERE command = ?");
        preparedStatement.setString(1, obj.getCommand());

        preparedStatement.execute();


    }

}
