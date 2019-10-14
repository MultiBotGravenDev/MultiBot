package fr.gravendev.multibot.logs;

import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.LogsDAO;
import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.utils.Configuration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
        User user = message.getAuthor();
        Guild guild = message.getGuild();

        if (user.isBot()
                || !guild.getId().equals(Configuration.GUILD.getValue())) {
            return;
        }

        String userId = user.getId();
        String avatarUrl = user.getAvatarUrl();
        String userName = user.getName();
        String jumpUrl = message.getJumpUrl();
        MessageChannel channel = message.getChannel();

        if (channel.getName().startsWith("présentation-")) {
            MessageData messageData = new MessageData(message);
            logsDAO.save(messageData);

            MessageEmbed messageEmbed = new EmbedBuilder()
                    .setColor(Color.ORANGE)
                    .setAuthor(userName, avatarUrl)
                    .setDescription("Projet envoyé dans <#" + channel.getId() + ">")
                    .addField("Aller au message: ", "[Lien](" + jumpUrl + ")", false)
                    .setFooter("User ID: " + userId, avatarUrl).build();

            sendMessageIfNotNull(guild, messageEmbed);
        }

        List<Role> mentionedRoleList = message.getMentionedRoles();

        if (mentionedRoleList.size() > 0) {
            String mentionedRoles = mentionedRoleList.stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(", "));
            MessageEmbed messageEmbed = new EmbedBuilder().setColor(Color.MAGENTA)
                    .setAuthor(userName, avatarUrl)
                    .setDescription("Rôle mentionné dans <#" + channel.getId() + ">")
                    .addField("Rôle mentionné: ", mentionedRoles, false)
                    .addField("Aller au message: ", "[Lien](" + jumpUrl + ")", false)
                    .setFooter("User ID: " + userId, avatarUrl).build();

            sendMessageIfNotNull(guild, messageEmbed);
        }
    }

    private void sendMessageIfNotNull(Guild guild, MessageEmbed messageEmbed) {
        String logs = Configuration.LOGS.getValue();
        TextChannel logsChannel = guild.getTextChannelById(logs);

        if (logsChannel != null) {
            logsChannel.sendMessage(messageEmbed).queue();
        }
    }
}
