package fr.gravendev.multibot.database;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    private HikariDataSource dataSource;

    public DatabaseConnection(DatabaseConnectionBuilder builder) {

        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://" + builder.host + ":3306/" + builder.database);
        dataSource.setUsername(builder.user);
        dataSource.setPassword(builder.password);
        dataSource.addDataSourceProperty("autoReconnect", true);
        dataSource.addDataSourceProperty("tcpKeepAlive", true);
        dataSource.addDataSourceProperty("serverTimezone", "Europe/Paris");
        dataSource.setMaximumPoolSize(15);
        dataSource.setMinimumIdle(0);
        dataSource.setIdleTimeout(1);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
