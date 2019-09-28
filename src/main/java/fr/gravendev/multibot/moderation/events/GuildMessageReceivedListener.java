package fr.gravendev.multibot.moderation.events;

import fr.gravendev.multibot.database.dao.BadWordsDAO;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.ImmunisedIdDAO;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.moderation.InfractionType;
import fr.gravendev.multibot.utils.Configuration;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.Date;

public class GuildMessageReceivedListener implements Listener<GuildMessageReceivedEvent> {
    private final BadWordsDAO badWordsDAO;
    private final InfractionDAO infractionDAO;
    private final ImmunisedIdDAO immunisedIdsDAO;

    public GuildMessageReceivedListener(DAOManager daoManager) {
        this.badWordsDAO = daoManager.getBadWordsDAO();
        this.infractionDAO = daoManager.getInfractionDAO();
        this.immunisedIdsDAO = daoManager.getImmunisedIdDAO();
    }


    @Override
    public Class<GuildMessageReceivedEvent> getEventClass() {
        return GuildMessageReceivedEvent.class;
    }

    @Override
    public void executeListener(GuildMessageReceivedEvent event) {
        if (!event.getMessage().getGuild().getId().equals(Configuration.GUILD.getValue()) || isImmunised(event.getMessage().getMember())) {
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

    private void warnForBadWordIfNeeded(Message message) {
        for (String badWord : this.badWordsDAO.get("").getBadWords().split(" ")) {
            if (badWord.isEmpty()) {
                continue;
            }
            computeBadWord(badWord, message);
        }
    }

    private void computeBadWord(String badWord, Message message) {
        String messageToCheck = " " + message.getContentDisplay().toLowerCase() + " ";
        String lowerCasedBadWord = badWord.toLowerCase();

        if (messageToCheck.contains(" " + lowerCasedBadWord + " ")) {
            warn(message, "Bad word");
        }
    }

    private void warnForDiscordInviteIfNeeded(Message message) {
        TextChannel textChannel = message.getTextChannel();
        String channelId = textChannel.getId();
        if (message.getContentDisplay().toLowerCase().contains("discord.gg/") &&
                !channelId.equals(Configuration.PROJECTS.getValue()) && channelId.equals(Configuration.PROJECTS_MINECRAFT.getValue())) {
            warn(message, "Posted invite");
            message.delete().queue();
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

    // TODO Refactor this, that's a too long and messy
    private void warn(Message message, String reason) {
        User user = message.getAuthor();
        Guild guild = message.getGuild();

        InfractionData data = new InfractionData(user.getId(), user.getId(), InfractionType.WARN, reason, new Date(), null);

        infractionDAO.save(data);
        String logs = Configuration.LOGS.getValue();

        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED)
                .setAuthor("[WARN] " + user.getAsTag(), user.getAvatarUrl())
                .addField("Utilisateur:", user.getAsMention(), true)
                .addField("Modérateur:", message.getJDA().getSelfUser().getAsMention(), true)
                .addField("Raison:", reason, true);

        TextChannel logsChannel = guild.getTextChannelById(logs);
        if (logsChannel != null) {
            logsChannel.sendMessage(embedBuilder.build()).queue();
        }

        message.getChannel().sendMessage(Utils.getWarnEmbed(user, reason)).queue();
    }
}
