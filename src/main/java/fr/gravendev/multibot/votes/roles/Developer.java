package fr.gravendev.multibot.votes.roles;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;

import java.awt.*;

public class Developer implements Role {

    private final GuildIdDAO guildIdDAO;

    public Developer(DatabaseConnection databaseConnection) {
        this.guildIdDAO = new GuildIdDAO(databaseConnection);
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
        return this.guildIdDAO.get("developpeur").id;
    }

    @Override
    public Color getColor() {
        return Color.BLUE;
    }

}
