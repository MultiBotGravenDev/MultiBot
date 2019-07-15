package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.data.WelcomeMessageData;
import fr.gravendev.multibot.database.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WelcomeMessageDAO extends DAO<WelcomeMessageData> {

    public WelcomeMessageDAO(Connection connection) {
        super(connection);
    }

    @Override
    public boolean save(WelcomeMessageData obj) {
        return false;
    }

    @Override
    public WelcomeMessageData get(String value) {
        return null;
    }

    @Override
    public void delete(WelcomeMessageData obj) {

    }

}
