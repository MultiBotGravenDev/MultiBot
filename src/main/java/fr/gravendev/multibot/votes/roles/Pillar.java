package fr.gravendev.multibot.votes.roles;

import fr.gravendev.multibot.utils.Configuration;

import java.awt.*;

public class Pillar implements Role {

    @Override
    public String getRoleName() {
        return "pilier de la commu";
    }

    @Override
    public String getChannelName() {
        return "votes-piliers";
    }

    @Override
    public String getRoleId() {
        return Configuration.PILIER.getValue();
    }

    @Override
    public Color getColor() {
        return Color.YELLOW;
    }

}
