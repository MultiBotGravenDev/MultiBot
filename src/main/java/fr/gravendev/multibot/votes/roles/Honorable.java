package fr.gravendev.multibot.votes.roles;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;

import java.awt.*;

public class Honorable implements Role {

    private final GuildIdDAO guildIdDAO;

    public Honorable(DatabaseConnection databaseConnection) {
        this.guildIdDAO = new GuildIdDAO(databaseConnection);
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
        return guildIdDAO.get("honorable").id;
    }

    @Override
    public Color getColor() {
        return Color.CYAN;
    }

}
