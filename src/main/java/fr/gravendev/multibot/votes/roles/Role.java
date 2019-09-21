package fr.gravendev.multibot.votes.roles;

import java.awt.Color;

public interface Role {
    String getRoleName();

    String getChannelName();

    long getRoleId();

    Color getColor();
}
