package fr.gravendev.multibot.database;

import java.sql.Connection;

public abstract class DAO<T> {

    private final Connection connection;

    public DAO(Connection connection) {
        this.connection = connection;
    }

    public abstract boolean create(T obj);

    public abstract boolean update(T obj);

    public abstract T get(String discordID);

    public abstract void delete(T obj);

    protected Connection getConnection() {
        return connection;
    }
}
