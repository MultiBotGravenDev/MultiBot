package fr.gravendev.multibot.votes.roles;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;

import java.awt.*;
import java.sql.SQLException;

public class Pillar implements Role {

    private final DatabaseConnection databaseConnection;

    public Pillar(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
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
        try {
            return new GuildIdDAO(this.databaseConnection).get("pilier").id;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    @Override
    public Color getColor() {
        return Color.YELLOW;
    }

}
