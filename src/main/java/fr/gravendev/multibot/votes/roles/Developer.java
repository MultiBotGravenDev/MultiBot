package fr.gravendev.multibot.votes.roles;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;

import java.awt.*;

public class Developer implements Role {

    private final DatabaseConnection databaseConnection;

    public Developer(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getRoleName() {
        return "développeur";
    }

    @Override
    public String getChannelName() {
        return "vote-developpeur";
    }

    @Override
    public long getRoleId() {
        return new GuildIdDAO(this.databaseConnection).get("developpeur").id;
    }

    @Override
    public Color getColor() {
        return Color.BLUE;
    }

}
