package fr.gravendev.multibot.utils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.awt.*;

public class Utils {

    private static final PeriodFormatter periodParser = new PeriodFormatterBuilder()
            .appendWeeks().appendSuffix("w")
            .appendDays().appendSuffix("d")
            .appendHours().appendSuffix("h")
            .appendMinutes().appendSuffix("m")
            .appendSeconds().appendSuffix("s")
            .toFormatter();

    public static Period getTimeFromInput(String input) {
        try {
            return periodParser.parsePeriod(input);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static MessageEmbed buildEmbed(Color color, String message) {
        return new EmbedBuilder().setColor(color).setDescription(message).build();
    }

    public static MessageEmbed banEmbed(User user, String reason) {
        return new EmbedBuilder().setColor(Color.DARK_GRAY).setDescription(user.getAsTag()+" a été bannis ! \n" +
                "Raison: "+reason).build();
    }

    public static MessageEmbed kickEmbed(User user, String reason) {
        return new EmbedBuilder().setColor(Color.DARK_GRAY).setDescription(user.getAsTag()+" a été éjecté ! \n" +
                "Raison: "+reason).build();
    }

    public static MessageEmbed muteEmbed(User user, String reason) {
        return new EmbedBuilder().setColor(Color.DARK_GRAY).setDescription(user.getAsTag()+" a été mute ! \n" +
                "Raison: "+reason).build();
    }

    public static MessageEmbed warnEmbed(User user, String reason) {
        return new EmbedBuilder().setColor(Color.DARK_GRAY).setDescription(user.getAsTag()+" a été avertis ! \n" +
                "Raison: "+reason).build();
    }

}
