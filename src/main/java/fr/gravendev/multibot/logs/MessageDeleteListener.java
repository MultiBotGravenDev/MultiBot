package fr.gravendev.multibot.logs;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.database.dao.LogsDAO;
import fr.gravendev.multibot.database.data.GuildIdsData;
import fr.gravendev.multibot.events.Listener;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;

import java.awt.*;
import java.util.Date;

public class MessageDeleteListener implements Listener<MessageDeleteEvent> {

    private final GuildIdDAO guildIdDAO;
    private final LogsDAO logsDAO;

    public MessageDeleteListener(DatabaseConnection databaseConnection) {
        this.guildIdDAO = new GuildIdDAO(databaseConnection);
        this.logsDAO = new LogsDAO(databaseConnection);
    }

    @Override
    public Class<MessageDeleteEvent> getEventClass() {
        return MessageDeleteEvent.class;
    }

    // TODO Refactor that by splitting it in different methods
    @Override
    public void executeListener(MessageDeleteEvent event) {
        TextChannel channel = event.getTextChannel();
        String channelName = channel.getName();

        if (!channelName.startsWith("présentation")){
            return;
        }

        String messageId = event.getMessageId();
        MessageData messageData = logsDAO.get(messageId);

        if (messageData == null){
            return;
        }

        GuildIdsData logs = guildIdDAO.get("logs");
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
        long logsId = logs.id;
        MessageEmbed embed = embedBuilder.build();

        TextChannel logsChannel = guild.getTextChannelById(logsId);
        logsChannel.sendMessage(embed).queue();
    }
}
