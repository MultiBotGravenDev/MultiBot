package fr.gravendev.multibot.database.dao;

import java.sql.Connection;

public abstract class DAO<T> {

    final Connection connection;

    DAO(Connection connection) {
        this.connection = connection;
    }

    public boolean save(T obj) {
        return true;
    }

    public T get(String value) {return null;}

    public void delete(T obj) {}

}
