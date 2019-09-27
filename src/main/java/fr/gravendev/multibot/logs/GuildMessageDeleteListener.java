package fr.gravendev.multibot.logs;

import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.LogsDAO;
import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.utils.Configuration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;

import java.awt.*;
import java.util.Date;

public class GuildMessageDeleteListener implements Listener<GuildMessageDeleteEvent> {

    private final LogsDAO logsDAO;

    public GuildMessageDeleteListener(DAOManager daoManager) {
        this.logsDAO = daoManager.getLogsDAO();
    }

    @Override
    public Class<GuildMessageDeleteEvent> getEventClass() {
        return GuildMessageDeleteEvent.class;
    }

    // TODO Refactor that by splitting it in different methods
    @Override
    public void executeListener(GuildMessageDeleteEvent event) {
        TextChannel channel = event.getChannel();
        String channelName = channel.getName();

        if (!channelName.startsWith("présentation") || !channel.getGuild().getId().equals(Configuration.GUILD.getValue())) {
            return;
        }

        String messageId = event.getMessageId();
        MessageData messageData = logsDAO.get(messageId);

        if (messageData == null) {
            return;
        }

        String logs = Configuration.LOGS.getValue();
        JDA jda = event.getJDA();
        String discordID = messageData.getDiscordID();
        User user = jda.getUserById(discordID);
        String content = messageData.getContent();
        String userName = user.getName();
        String userAvatarUrl = user.getAvatarUrl();
        String channelAsMention = channel.getAsMention();
        String description = "Projet supprimé dans " + channelAsMention;
        long messageCreation = messageData.getCreation();
        Date messageCreationDate = new Date(messageCreation);
        // TODO Find a better name for this
        String stringifiedMessageCreationDate = messageCreationDate.toString();
        int contentLength = content.length();
        int contentSize = Math.min(contentLength, 20);
        String displayedContent = content.substring(0, contentSize);
        String userId = user.getId();

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setAuthor(userName, userAvatarUrl)
                .setDescription(description)
                .addField("Date de création :", stringifiedMessageCreationDate, false)
                .addField("Contenu :", displayedContent, false)
                .setFooter("User ID: " + userId, userAvatarUrl);

        Guild guild = event.getGuild();
        MessageEmbed embed = embedBuilder.build();

        TextChannel logsChannel = guild.getTextChannelById(logs);
        logsChannel.sendMessage(embed).queue();
    }
}
