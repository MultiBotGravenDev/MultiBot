package fr.gravendev.multibot.commands.commands;

import java.awt.*;

public enum CommandCategory {

    SYSTEM("Systèmes", Color.RED),
    UTILS("Utilitaires", Color.CYAN),
    MODERATION("Modérations", Color.GREEN),
    EXPERIENCE("Expériences", Color.YELLOW),
    NONE("Non définies", Color.DARK_GRAY);

    private final String name;
    private final Color color;

    CommandCategory(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }
}
