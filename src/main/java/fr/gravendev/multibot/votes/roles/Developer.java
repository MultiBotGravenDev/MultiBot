package fr.gravendev.multibot.votes.roles;

import fr.gravendev.multibot.utils.Configuration;

import java.awt.*;

public class Developer implements Role {

    @Override
    public String getRoleName() {
        return "développeur";
    }

    @Override
    public String getChannelName() {
        return "vote-developpeur";
    }

    @Override
    public String getRoleId() {
        return Configuration.DEVELOPPEUR.getValue();
    }

    @Override
    public Color getColor() {
        return Color.BLUE;
    }

}
