package fr.gravendev.multibot.votes.roles;

import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.GuildIdDAO;

import java.awt.*;

public class Honorable implements Role {

    private final GuildIdDAO guildIdDAO;

    public Honorable(DAOManager daoManager) {
        this.guildIdDAO = daoManager.getGuildIdDAO();
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
