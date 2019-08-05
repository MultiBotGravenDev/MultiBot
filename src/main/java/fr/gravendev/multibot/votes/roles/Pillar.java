package fr.gravendev.multibot.votes.roles;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;

import java.awt.*;

public class Pillar implements Role {

    private final GuildIdDAO guildIdDAO;

    public Pillar(DatabaseConnection databaseConnection) {
        this.guildIdDAO = new GuildIdDAO(databaseConnection);
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
        return this.guildIdDAO.get("pilier").id;
    }

    @Override
    public Color getColor() {
        return Color.YELLOW;
    }

}
