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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GuildMessageReceivedListener implements Listener<GuildMessageReceivedEvent> {

    private final BadWordsDAO badWordsDAO;
    private final InfractionDAO infractionDAO;
    private final ImmunisedIdDAO immunisedIdsDAO;

    private final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
    private final Map<String, Integer> spamCounter = Collections.synchronizedMap(new HashMap<>());

    /**
    * Represents the minimum amount of messages to be sent by the user to be considered as spam.
    */
    private static final int MIN_MESSAGES_FOR_SPAM = 5;

    /**
     * Represents the maximum amount of characters to consider a message like a potential spam.
     */
    private static final int MAX_CHAR_FOR_SPAM = 5;

    /**
     * Represents the interval during which {@value MIN_MESSAGES_FOR_SPAM} messages must be sent to be considered as spam.
     */
    private static final int SPAM_INTERVAL = 3;

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
        warnForSpamIfNeeded(message);
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
        if (message.getChannel().getName().contains("présentation")) {
            return;
        }

        if (message.getContentDisplay().toLowerCase().contains("discord.gg/")) {
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

    private void warnForCapitalsLettersIfNeeded(Message message) {
        String content = removeEmojisFromMessage(message);
        content = removeMentionsFromMessage(content, message);

        int capitalLettersCount = countCapitalLetters(content);
        int length = content.length();

        if (length >= 8 && capitalLettersCount * 100 / length >= 75) {
            warn(message, "Capital letters");
        }
    }

    /**
     * Warns the author of the {@link Message} if its last {@value MIN_MESSAGES_FOR_SPAM} messages
     * are sent in less than {@value SPAM_INTERVAL} seconds and if they all contain
     * {@value MAX_CHAR_FOR_SPAM} characters or less.
     *
     * @param message The message to analyze
     */
    private void warnForSpamIfNeeded(Message message) {
        String authorId = message.getAuthor().getId();
        if(message.getContentDisplay().length() > MAX_CHAR_FOR_SPAM) return;
        if(spamCounter.containsKey(authorId)){
            if(spamCounter.get(authorId) == MIN_MESSAGES_FOR_SPAM){
                warn(message, "Spam");
                spamCounter.remove(authorId);
            }else{
                spamCounter.put(authorId, spamCounter.get(authorId) + 1);
            }
        }else{
            scheduledExecutorService.schedule(() -> spamCounter.remove(authorId), SPAM_INTERVAL, TimeUnit.SECONDS);
            spamCounter.put(authorId, 1);
        }
    }

    // TODO: Try to refactor this using regex
    private int countCapitalLetters(String text) {
        int capitalLettersCount = 0;

        for (char letter : text.toCharArray()) {
            if (Character.isUpperCase(letter)) {
                ++capitalLettersCount;
            }
        }
        return capitalLettersCount;
    }

    private String removeMentionsFromMessage(String content, Message message) {

        for (Member mentionedMember : message.getMentionedMembers()) {
            content = content.replace(mentionedMember.getEffectiveName(), "");
        }

        return content;
    }

    // TODO: Refactor this, that's a too long and messy
    private void warn(Message message, String reason) {
        User user = message.getAuthor();
        Guild guild = message.getGuild();

        InfractionData data = new InfractionData(user.getId(), user.getId(), InfractionType.WARN, reason, new Date(), null);

        infractionDAO.save(data);
        String logs = Configuration.SANCTIONS.getValue();

        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED)
                .setAuthor("[WARN] " + user.getAsTag(), user.getAvatarUrl())
                .addField("Utilisateur :", user.getAsMention(), true)
                .addField("Modérateur :", message.getJDA().getSelfUser().getAsMention(), true)
                .addField("Raison :", reason, true);

        TextChannel logsChannel = guild.getTextChannelById(logs);
        if (logsChannel != null) {
            logsChannel.sendMessage(embedBuilder.build()).queue();
        }

        message.getChannel().sendMessage(Utils.getWarnEmbed(user, reason)).queue();
    }
}
