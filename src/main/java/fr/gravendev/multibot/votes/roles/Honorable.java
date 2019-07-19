package fr.gravendev.multibot.votes.roles;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;

import java.awt.*;
import java.sql.SQLException;

public class Honorable implements Role {

    private final DatabaseConnection databaseConnection;

    public Honorable(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getRoleName() {
        return "honorable";
    }

    @Override
    public String getChannelName() {
        return "vote-honorable";
    }

    @Override
    public long getRoleId() {
        try {
            return new GuildIdDAO(this.databaseConnection.getConnection()).get("honorable").id;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    @Override
    public Color getColor() {
        return Color.CYAN;
    }

}
