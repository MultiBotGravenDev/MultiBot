package fr.gravendev.multibot.votes.roles;

import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.GuildIdDAO;

import java.awt.*;

public class Developer implements Role {

    private final GuildIdDAO guildIdDAO;

    public Developer(DAOManager daoManager) {
        this.guildIdDAO = daoManager.getGuildIdDAO();
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
        return guildIdDAO.get("developpeur").id;
    }

    @Override
    public Color getColor() {
        return Color.BLUE;
    }

}
