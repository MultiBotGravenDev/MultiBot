package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class DAO<T> {

     private final DatabaseConnection databaseConnection;

    public DAO(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public abstract boolean save(T obj) throws SQLException;
    public abstract T get(String value) throws SQLException;
    public abstract void delete(T obj) throws SQLException;
    
    public Connection getConnection() throws SQLException {
        return databaseConnection.getConnection();
    }

    public void closeConnection(Connection connection) throws SQLException {
        if(!connection.isClosed())
            connection.close();
    }

}