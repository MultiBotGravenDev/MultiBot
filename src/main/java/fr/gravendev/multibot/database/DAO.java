package fr.gravendev.multibot.database;

import java.sql.Connection;

public abstract class DAO<T> {

    protected final Connection connection;

    public DAO(Connection connection) {
        this.connection = connection;
    }

    public abstract boolean save(T obj);

    public abstract T get(String value);

    public abstract void delete(T obj);

}
