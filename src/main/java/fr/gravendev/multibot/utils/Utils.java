package fr.gravendev.multibot.utils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Utils {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy à HH:mm:ss");
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm:ss");

    public static SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public static DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    public static MessageEmbed buildEmbed(Color color, String message) {
        return new EmbedBuilder()
                .setColor(color)
                .setDescription(message)
                .build();
    }

    private static String stringifyEndDate(Date end){
        if (end != null){
            return getDateFormat().format(end);
        }
        return "Jamais";
    }

    private static String makeDescription(User user, String action, String reason){
        String userTag = user.getAsTag();

        return TextFormatter.formatLines(
                userTag + " a été " + action + "!",
                "Raison: " + reason
        ).toString();
    }

    private static MessageEmbed makeEmbed(User user, String action, String reason){
        String description = makeDescription(user, action, reason);
        return buildEmbed(Color.DARK_GRAY, description);
    }

    private static String makeDescription(User user, String action, String reason, Date end){
        // TODO Find a better name than beginning if possible
        String beginning = makeDescription(user, action, reason);
        String endDate = stringifyEndDate(end);

        return TextFormatter.formatLines(
                beginning,
                "Fin le: " + endDate
        ).toString();
    }

    private static MessageEmbed makeEmbed(User user, String action, String reason, Date end){
        String description = makeDescription(user, action, reason, end);
        return buildEmbed(Color.DARK_GRAY, description);
    }

    // TODO Put a verb at the beginning of some of those methods (get for example)
    public static MessageEmbed banEmbed(User user, String reason, Date end) {
        return makeEmbed(user, "banni", reason, end);
    }

    public static MessageEmbed kickEmbed(User user, String reason) {
        return makeEmbed(user, "éjecté", reason);
    }

    public static MessageEmbed muteEmbed(User user, String reason, Date end) {
        return makeEmbed(user, "rendu muet", reason, end);
    }

    public static MessageEmbed warnEmbed(User user, String reason) {
        return makeEmbed(user, "averti", reason);
    }

    private static final Map<String, Long> TIME_FORMAT = new HashMap<>();

    static {
        TIME_FORMAT.put("y", 31_536_000_000L);
        TIME_FORMAT.put("M", 2_592_000_000L);
        TIME_FORMAT.put("d", 86_400_000L);
        TIME_FORMAT.put("h", 3_600_000L);
        TIME_FORMAT.put("m", 60_000L);
        TIME_FORMAT.put("s", 1_000L);
        TIME_FORMAT.put("ms", 1L);
    }

    // TODO To refactor!!
    public static long parsePeriod(String parser) {
        Objects.requireNonNull(parser, "Parser cannot be nul");

        char[] chars = parser.toCharArray();

        StringBuilder timeBuilder = new StringBuilder();
        StringBuilder keyBuilder = new StringBuilder();

        long time = 0;

        for (int i = 0; i < chars.length; i++) {
            if (Character.isDigit(chars[i])){
                timeBuilder.append(chars[i]);
            } else {
                keyBuilder.append(chars[i]);
                if (i == chars.length - 1 || Character.isDigit(chars[i + 1])) {
                    Long mul = TIME_FORMAT.get(keyBuilder.toString());

                    if (mul == null)
                        return -1;

                    time += (Integer.parseInt(timeBuilder.toString()) * mul);

                    timeBuilder = new StringBuilder();
                    keyBuilder = new StringBuilder();
                }
            }
        }

        if (keyBuilder.length() > 0 || timeBuilder.length() > 0){
            return -1;
        }

        return time;
    }

}
