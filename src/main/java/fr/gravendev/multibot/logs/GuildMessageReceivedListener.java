package fr.gravendev.multibot.logs;

import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.LogsDAO;
import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.utils.Configuration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class GuildMessageReceivedListener implements Listener<GuildMessageReceivedEvent> {

    private final LogsDAO logsDAO;

    public GuildMessageReceivedListener(DAOManager daoManager) {
        this.logsDAO = daoManager.getLogsDAO();
    }

    @Override
    public Class<GuildMessageReceivedEvent> getEventClass() {
        return GuildMessageReceivedEvent.class;
    }

    @Override
    public void executeListener(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        MessageChannel channel = message.getChannel();
        User user = message.getAuthor();
        if (user.isBot() || !message.getGuild().getId().equals(Configuration.GUILD.getValue())) return;

        if (channel.getName().startsWith("présentation-")) {

            MessageData messageData = new MessageData(message);
            logsDAO.save(messageData);

            String logs = Configuration.LOGS.getValue();

            EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.ORANGE)
                    .setAuthor(user.getName(), user.getAvatarUrl())
                    .setDescription("Projet envoyé dans <#" + channel.getId() + ">")
                    .addField("Aller au message: ", "[Lien](" + message.getJumpUrl() + ")", false)
                    .setFooter("User ID: " + user.getId(), user.getAvatarUrl());

            TextChannel logsChannel = message.getGuild().getTextChannelById(logs);
            if (logsChannel != null) {
                logsChannel.sendMessage(embedBuilder.build()).queue();
            }
        }

        List<Role> mentionedRoles = message.getMentionedRoles();
        if (mentionedRoles.size() > 0) {

            String logs = Configuration.LOGS.getValue();

            EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.MAGENTA)
                    .setAuthor(user.getName(), user.getAvatarUrl())
                    .setDescription("Rôle mentionné dans <#" + channel.getId() + ">")
                    .addField("Rôle mentionné: ", mentionedRoles.stream().map(Role::getName).collect(Collectors.joining(", ")), false)
                    .addField("Aller au message: ", "[Lien](" + message.getJumpUrl() + ")", false)
                    .setFooter("User ID: " + user.getId(), user.getAvatarUrl());

            TextChannel logsChannel = message.getGuild().getTextChannelById(logs);
            if (logsChannel != null) {
                logsChannel.sendMessage(embedBuilder.build()).queue();
            }

        }

    }
}
