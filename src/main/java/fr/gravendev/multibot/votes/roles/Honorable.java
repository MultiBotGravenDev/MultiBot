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
        return new GuildIdDAO(this.databaseConnection).get("honorable").id;
    }

    @Override
    public Color getColor() {
        return Color.CYAN;
    }

}
