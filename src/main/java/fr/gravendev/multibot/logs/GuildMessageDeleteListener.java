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

        MessageData messageData = logsDAO.get(event.getMessageId());

        if (messageData == null) {
            return;
        }

        String logs = Configuration.LOGS.getValue();
        String discordID = messageData.getDiscordID();
        User user = event.getJDA().getUserById(discordID);
        String content = messageData.getContent();
        // TODO Catch case when user is null
        String userName = user.getName();
        String userAvatarUrl = user.getAvatarUrl();
        String description = "Projet supprimé dans " + channel.getAsMention();
        // TODO Find a better name for this
        String messageCreationDate = new Date(messageData.getCreation()).toString();
        int contentSize = Math.min(content.length(), 20);
        String displayedContent = content.substring(0, contentSize);
        String userId = user.getId();

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setAuthor(userName, userAvatarUrl)
                .setDescription(description)
                .addField("Date de création :", messageCreationDate, false)
                .addField("Contenu :", displayedContent, false)
                .setFooter("User ID: " + userId, userAvatarUrl);

        Guild guild = event.getGuild();
        MessageEmbed embed = embedBuilder.build();

        TextChannel logsChannel = guild.getTextChannelById(logs);
        logsChannel.sendMessage(embed).queue();
    }
}
