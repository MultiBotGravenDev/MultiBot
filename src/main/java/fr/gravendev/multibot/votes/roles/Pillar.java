package fr.gravendev.multibot.votes.roles;

import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.GuildIdDAO;

import java.awt.*;

public class Pillar implements Role {

    private final GuildIdDAO guildIdDAO;

    public Pillar(DAOManager daoManager) {
        this.guildIdDAO = daoManager.getGuildIdDAO();
    }

    @Override
    public String getRoleName() {
        return "pilier de la commu";
    }

    @Override
    public String getChannelName() {
        return "votes-piliers";
    }

    @Override
    public long getRoleId() {
        return guildIdDAO.get("pilier").id;
    }

    @Override
    public Color getColor() {
        return Color.YELLOW;
    }

}
