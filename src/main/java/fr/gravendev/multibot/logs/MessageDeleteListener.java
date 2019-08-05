package fr.gravendev.multibot.logs;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.database.dao.LogsDAO;
import fr.gravendev.multibot.database.data.GuildIdsData;
import fr.gravendev.multibot.events.Listener;
import net.dv8tion.jda.core.EmbedBuilder;
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

    @Override
    public void executeListener(MessageDeleteEvent event) {

        if (!event.getTextChannel().getName().startsWith("présentation")) return;

        MessageData messageData = this.logsDAO.get(event.getMessageId());

        if (messageData == null) return;

        GuildIdsData logs = this.guildIdDAO.get("logs");
        User user = event.getJDA().getUserById(messageData.getDiscordID());

        String content = messageData.getContent();
        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.ORANGE)
                .setAuthor(user.getName(), user.getAvatarUrl())
                .setDescription("Projet supprimé dans " + event.getTextChannel().getAsMention())
                .addField("Date de création :", new Date(messageData.getCreation()).toString(), false)
                .addField("Contenu :", content.substring(0, Math.min(content.length(), 20)), false)
                .setFooter("User ID: " + user.getId(), user.getAvatarUrl());

        TextChannel logsChannel = event.getGuild().getTextChannelById(logs.id);
        logsChannel.sendMessage(embedBuilder.build()).queue();

    }

}
