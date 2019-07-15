package fr.gravendev.multibot.database;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    private HikariDataSource dataSource;

    public DatabaseConnection(String host, String user, String password, String database) {

        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://"+host+":3306/"+database);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
