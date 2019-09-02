package fr.gravendev.multibot.logs;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.database.dao.LogsDAO;
import fr.gravendev.multibot.database.data.GuildIdsData;
import fr.gravendev.multibot.events.Listener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class MessageReceivedListener implements Listener<MessageReceivedEvent> {

    private final DatabaseConnection databaseConnection;

    public MessageReceivedListener(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Class<MessageReceivedEvent> getEventClass() {
        return MessageReceivedEvent.class;
    }

    @Override
    public void executeListener(MessageReceivedEvent event) {
        Message message = event.getMessage();
        MessageChannel channel = message.getChannel();
        User user = message.getAuthor();
        if (user.isBot()) return;

        if (channel.getName().startsWith("présentation-")) {

            LogsDAO logsDAO = new LogsDAO(databaseConnection);
            MessageData messageData = new MessageData(message);
            logsDAO.save(messageData);

            GuildIdDAO guildIdDAO = new GuildIdDAO(databaseConnection);
            GuildIdsData logs = guildIdDAO.get("logs");

            EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.ORANGE)
                    .setAuthor(user.getName(), user.getAvatarUrl())
                    .setDescription("Projet envoyé dans <#" + channel.getId() + ">")
                    .addField("Aller au message: ", "[Lien](" + message.getJumpUrl() + ")", false)
                    .setFooter("User ID: " + user.getId(), user.getAvatarUrl());

            TextChannel logsChannel = message.getGuild().getTextChannelById(logs.id);
            if (logsChannel != null) {
                logsChannel.sendMessage(embedBuilder.build()).queue();
            }
        }

        List<Role> mentionedRoles = message.getMentionedRoles();
        if (mentionedRoles.size() > 0) {

            GuildIdDAO guildIdDAO = new GuildIdDAO(databaseConnection);
            GuildIdsData logs = guildIdDAO.get("logs");

            EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.MAGENTA)
                    .setAuthor(user.getName(), user.getAvatarUrl())
                    .setDescription("Rôle mentionné dans <#" + channel.getId() + ">")
                    .addField("Rôle mentionné: ", mentionedRoles.stream().map(Role::getName).collect(Collectors.joining(", ")), false)
                    .addField("Aller au message: ", "[Lien](" + message.getJumpUrl() + ")", false)
                    .setFooter("User ID: " + user.getId(), user.getAvatarUrl());

            TextChannel logsChannel = message.getGuild().getTextChannelById(logs.id);
            if (logsChannel != null) {
                logsChannel.sendMessage(embedBuilder.build()).queue();
            }

        }

    }
}
