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
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;

import java.awt.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GuildMessageReceivedListener implements Listener<GuildMessageReceivedEvent> {

    private final BadWordsDAO badWordsDAO;
    private final InfractionDAO infractionDAO;
    private final ImmunisedIdDAO immunisedIdsDAO;

    private final ScheduledExecutorScheduler scheduledExecutorScheduler = new ScheduledExecutorScheduler();
    private final Map<String, Integer> spamCounter = new HashMap<>();

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

    private void warnForSpamIfNeeded(Message message){
        if(message.getContentDisplay().length() > 5) return;
        if(spamCounter.containsKey(message.getAuthor().getId())){
            if(spamCounter.get(message.getAuthor().getId()) == 5){
                warn(message, "Spam");
                spamCounter.remove(message.getAuthor().getId());
            }else{
                spamCounter.put(message.getAuthor().getId(), spamCounter.get(message.getAuthor().getId()) + 1);
            }
        }else{
            scheduledExecutorScheduler.schedule(() -> spamCounter.remove(message.getAuthor().getId()), 3, TimeUnit.SECONDS);
            spamCounter.put(message.getAuthor().getId(), 1);
        }
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

    private String removeMentionsFromMessage(String content, Message message) {

        for (Member mentionedMember : message.getMentionedMembers()) {
            content = content.replace(mentionedMember.getEffectiveName(), "");
        }

        return content;
    }

    // TODO Refactor this, that's a too long and messy
    private void warn(Message message, String reason) {
        User user = message.getAuthor();
        Guild guild = message.getGuild();

        InfractionData data = new InfractionData(user.getId(), user.getId(), InfractionType.WARN, reason, new Date(), null);

        infractionDAO.save(data);
        String logs = Configuration.SANCTIONS.getValue();

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
