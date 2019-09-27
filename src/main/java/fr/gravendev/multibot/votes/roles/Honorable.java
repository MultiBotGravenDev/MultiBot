package fr.gravendev.multibot.votes.roles;

import fr.gravendev.multibot.utils.Configuration;

import java.awt.*;

public class Honorable implements Role {

    @Override
    public String getRoleName() {
        return "honorable";
    }

    @Override
    public String getChannelName() {
        return "vote-honorable";
    }

    @Override
    public String getRoleId() {
        return Configuration.HONORABLE.getValue();
    }

    @Override
    public Color getColor() {
        return Color.CYAN;
    }

}
