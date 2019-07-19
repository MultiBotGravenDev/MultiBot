package fr.gravendev.multibot.moderation;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.dao.LogsDAO;
import fr.gravendev.multibot.database.data.GuildIdsData;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.logs.MessageData;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.awt.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KickCommand implements CommandExecutor {

    private final DatabaseConnection databaseConnection;

    public KickCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getCommand() {
        return "kick";
    }

    @Override
    public String getDescription() {
        return "Ejecter un membre du discord.";
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.GUILD;
    }

    @Override
    public void execute(Message message, String[] args) {
        try {

            List<Member> mentionedMembers = message.getMentionedMembers();
            if (mentionedMembers.size() == 0) {
                message.getChannel().sendMessage("Usage: kick @Member").queue();
                return;
            }

            Guild guild = message.getGuild();

            Member member = mentionedMembers.get(0);
            Member bot = guild.getMember(message.getJDA().getSelfUser());

            User moderator = message.getAuthor();
            User kickedUser = member.getUser();

            String reason = "Non définie";
            if (args.length >= 2) {
                reason = Stream.of(args).skip(1).collect(Collectors.joining(" "));
            }

            if (!PermissionUtil.canInteract(bot, member)) {
                message.getChannel().sendMessage("Impossible d'éjecter cet utilisateur !").queue();
                return;
            }

            //guild.getController().kick(member, reason).queue();

            InfractionData data = new InfractionData(
                    kickedUser.getId(), moderator.getId(), InfractionType.KICK, reason, new Date(), null);
            InfractionDAO dao = new InfractionDAO(databaseConnection);
            dao.save(data);


            LogsDAO logsDAO = new LogsDAO(databaseConnection);
            MessageData messageData = new MessageData(message);
            logsDAO.save(messageData);

            GuildIdDAO guildIdDAO = new GuildIdDAO(databaseConnection);
            GuildIdsData logs = guildIdDAO.get("logs");

            EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED)
                    .setAuthor("[KICK] " +kickedUser.getAsTag(), kickedUser.getAvatarUrl())
                    .addField("Utilisateur:", kickedUser.getAsMention(), true)
                    .addField("Modérateur:", moderator.getAsMention(), true)
                    .addField("Raison:", reason, true);

            TextChannel logsChannel = guild.getTextChannelById(logs.id);
            logsChannel.sendMessage(embedBuilder.build()).queue();

            message.getChannel().sendMessage(member.getAsMention()+" a été éjecter !").queue();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isAuthorizedMember(Member member) {
        return member.hasPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public boolean isAuthorizedChannel(MessageChannel channel) {
        return true;
    }

}