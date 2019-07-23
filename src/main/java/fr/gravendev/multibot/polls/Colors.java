package fr.gravendev.multibot.polls;

import java.awt.*;
import java.util.Arrays;

public enum Colors {

    WHITE(Color.WHITE, "WHITE"),
    GREEN(Color.GREEN, "GREEN"),
    RED(Color.RED, "RED"),
    YELLOW(Color.YELLOW, "YELLOW"),
    CYAN(Color.CYAN, "CYAN"),
    GRAY(Color.GRAY, "GRAY"),
    MAGENTA(Color.MAGENTA, "MAGENTA"),
    ORANGE(Color.ORANGE, "ORANGE"),
    PINK(Color.PINK, "PINK"),
    BLUE(Color.BLUE, "BLUE");

    public final Color color;
    private final String text;

    Colors(Color color, String text) {
        this.color = color;
        this.text = text;
    }

    public static Colors fromString(String text) {
        return Arrays.stream(values())
                .filter(colors -> colors.text.equalsIgnoreCase(text))
                .findAny()
                .orElse(Colors.WHITE);
    }

}
