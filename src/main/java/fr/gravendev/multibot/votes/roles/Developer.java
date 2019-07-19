package fr.gravendev.multibot.votes.roles;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;

import java.awt.*;
import java.sql.SQLException;

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
        try {
            return new GuildIdDAO(this.databaseConnection.getConnection()).get("developpeur").id;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    @Override
    public Color getColor() {
        return Color.BLUE;
    }

}
