package fr.gravendev.multibot.logs;

import fr.gravendev.multibot.database.dao.DAOManager;
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

    private final LogsDAO logsDAO;
    private final GuildIdDAO guildIdDAO;

    public MessageReceivedListener(DAOManager daoManager) {
        this.logsDAO = daoManager.getLogsDAO();
        this.guildIdDAO = daoManager.getGuildIdDAO();
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

            MessageData messageData = new MessageData(message);
            logsDAO.save(messageData);

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
