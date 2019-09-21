package fr.gravendev.multibot.moderation.events;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.BadWordsDAO;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.database.dao.ImmunisedIdDAO;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.data.GuildIdsData;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.moderation.InfractionType;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.Date;

public class MessageReceivedListener implements Listener<GuildMessageReceivedEvent> {
    private final BadWordsDAO badWordsDAO;
    private final InfractionDAO infractionDAO;
    private final GuildIdDAO guildIdDAO;
    private final ImmunisedIdDAO immunisedIdsDAO;

    public MessageReceivedListener(DatabaseConnection databaseConnection) {
        this.badWordsDAO = new BadWordsDAO(databaseConnection);
        this.infractionDAO = new InfractionDAO(databaseConnection);
        this.guildIdDAO = new GuildIdDAO(databaseConnection);
        this.immunisedIdsDAO = new ImmunisedIdDAO(databaseConnection);
    }


    @Override
    public Class<GuildMessageReceivedEvent> getEventClass() {
        return GuildMessageReceivedEvent.class;
    }

    @Override
    public void executeListener(GuildMessageReceivedEvent event) {
        if (isImmunised(event.getMessage().getMember())) {
            return;
        }

        Message message = event.getMessage();

        warnForBadWordIfNeeded(message);
        warnForCapitalsLettersIfNeeded(message);
        warnForDiscordInviteIfNeeded(message);
    }

    private boolean isImmunised(Member member) {
        return member == null
                || member.getUser().isBot()
                || member.getRoles().stream().anyMatch(role -> this.immunisedIdsDAO.get("").immunisedIds.contains(role.getIdLong()))
                || member.hasPermission(Permission.ADMINISTRATOR);
    }

    private void warnForDiscordInviteIfNeeded(Message message) {
        if (message.getContentDisplay().contains("discord.gg")) {
            warn(message, "Posted invite");
            message.delete().queue();
        }
    }

    private void warnForBadWordIfNeeded(Message message) {
        for (String badWord : this.badWordsDAO.get("").getBadWords().split(" ")) {
            if (badWord.isEmpty()) {
                continue;
            }
            computeBadWord(badWord, message);
        }
    }

    private String removeEmojisFromMessage(Message message) {
        String content = message.getContentDisplay();

        for (Emote emote : message.getEmotes()) {
            content = content.replace(":" + emote.getName() + ":", "");
        }
        return content;
    }

    // TODO Try to refactor this using regex
    private int countCapitalLetters(String text) {
        int capitalLettersCount = 0;

        for (char letter : text.toCharArray()) {
            if (Character.isUpperCase(letter)) {
                ++capitalLettersCount;
            }
        }
        return capitalLettersCount;
    }

    private void warnForCapitalsLettersIfNeeded(Message message) {
        String content = removeEmojisFromMessage(message);
        int capitalLettersCount = countCapitalLetters(content);
        int length = content.length();

        if (length >= 8 && capitalLettersCount * 100 / length >= 75) {
            warn(message, "Capital letters");
        }
    }

    private void computeBadWord(String badWord, Message message) {
        if (message.getContentDisplay().toLowerCase().contains(badWord.toLowerCase())) {
            warn(message, "Bad word");
        }
    }

    // TODO Refactor this, that's a too long and messy
    private void warn(Message message, String reason) {
        User user = message.getAuthor();
        Guild guild = message.getGuild();

        InfractionData data = new InfractionData(user.getId(), user.getId(), InfractionType.WARN, "Bad word usage", new Date(), null);

        infractionDAO.save(data);
        GuildIdsData logs = guildIdDAO.get("logs");

        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED)
                .setAuthor("[WARN] " + user.getAsTag(), user.getAvatarUrl())
                .addField("Utilisateur:", user.getAsMention(), true)
                .addField("Modérateur:", message.getJDA().getSelfUser().getAsMention(), true)
                .addField("Raison:", reason, true);

        TextChannel logsChannel = guild.getTextChannelById(logs.id);
        if (logsChannel != null) {
            logsChannel.sendMessage(embedBuilder.build()).queue();
        }

        message.getChannel().sendMessage(Utils.getWarnEmbed(user, reason)).queue();
    }
}
