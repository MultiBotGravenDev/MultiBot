package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.data.CustomCommandData;
import fr.gravendev.multibot.utils.PreparedStatementBuilder;

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
        new PreparedStatementBuilder(connection)
                .prepareStatement("INSERT INTO custom_commands(command, text) VALUES(?, ?) ON DUPLICATE KEY UPDATE text = ?")
                .setString(obj.getCommand())
                .setString(obj.getText())
                .setString(obj.getText())
                .execute();
        // TODO Return execute??
        return true;
    }

    @Override
    public CustomCommandData get(String value, Connection connection) throws SQLException {
        ResultSet resultSet = new PreparedStatementBuilder(connection)
                .prepareStatement("SELECT * FROM custom_commands WHERE command = ? OR id = ?")
                .setString(value)
                .setString(value)
                .executeQuery();

        if (resultSet.next()) {
            String command = resultSet.getString("command");
            String text = resultSet.getString("text");

            return new CustomCommandData(command, text);
        }
        return null;
    }

    @Override
    public void delete(CustomCommandData obj, Connection connection) throws SQLException {
        new PreparedStatementBuilder(connection)
                .prepareStatement("DELETE FROM custom_commands WHERE command = ?")
                .setString(obj.getCommand())
                .execute();
    }
}
