package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class DAO<T> {

     private final DatabaseConnection databaseConnection;

    public DAO(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public boolean save(T obj) throws SQLException {
        Connection connection = getConnection();
        try {
            return save(obj, connection);
        }finally {
            closeConnection(connection);
        }
    }
    protected abstract boolean save(T obj, Connection connection) throws SQLException;

    public T get(String value) throws SQLException {
        Connection connection = getConnection();
        try {
            return get(value, connection);
        }finally {
            closeConnection(connection);
        }
    }
    protected abstract T get(String value, Connection connection) throws SQLException;

    public void delete(T obj) throws SQLException {
        Connection connection = getConnection();
        try {
            delete(obj, connection);
        }finally {
            closeConnection(connection);
        }
    }
    protected abstract void delete(T obj, Connection connection) throws SQLException;
    
    public Connection getConnection() throws SQLException {
        return databaseConnection.getConnection();
    }

    public void closeConnection(Connection connection) throws SQLException {
        if(!connection.isClosed())
            connection.close();
    }

}