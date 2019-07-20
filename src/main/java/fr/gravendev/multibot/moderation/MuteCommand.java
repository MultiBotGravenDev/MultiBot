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

public class MuteCommand implements CommandExecutor {

    private final DatabaseConnection databaseConnection;

    public MuteCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getCommand() {
        return "mute";
    }

    @Override
    public String getDescription() {
        return "Rendre muet un membre du discord.";
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.GUILD;
    }

    @Override
    public void execute(Message message, String[] args) {

            List<Member> mentionedMembers = message.getMentionedMembers();
            if (mentionedMembers.size() == 0) {
                message.getChannel().sendMessage("Usage: mute @Member").queue();
                return;
            }

            Guild guild = message.getGuild();

            Member member = mentionedMembers.get(0);
            Member bot = guild.getMember(message.getJDA().getSelfUser());

            User moderator = message.getAuthor();
            User mutedUser = member.getUser();

            String reason = "Non définie";
            if (args.length >= 2) {
                reason = Stream.of(args).skip(1).collect(Collectors.joining(" "));
            }

            if (!PermissionUtil.canInteract(bot, member)) {
                message.getChannel().sendMessage("Impossible de mute cet utilisateur !").queue();
                return;
            }

            GuildIdDAO guildIdDAO = new GuildIdDAO(databaseConnection);
            long mutedID = guildIdDAO.get("muted").id;
            Role muted = guild.getRoleById(mutedID);

            guild.getController().addRolesToMember(member, muted).queue();

            InfractionData data = new InfractionData(
                    mutedUser.getId(), moderator.getId(), InfractionType.MUTE, reason, new Date(), null);
            InfractionDAO dao = new InfractionDAO(databaseConnection);
            dao.save(data);


            LogsDAO logsDAO = new LogsDAO(databaseConnection);
            MessageData messageData = new MessageData(message);
            logsDAO.save(messageData);

            GuildIdsData logs = guildIdDAO.get("logs");

            EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED)
                    .setAuthor("[MUTE] " +mutedUser.getAsTag(), mutedUser.getAvatarUrl())
                    .addField("Utilisateur:", mutedUser.getAsMention(), true)
                    .addField("Modérateur:", moderator.getAsMention(), true)
                    .addField("Raison:", reason, true);

            TextChannel logsChannel = guild.getTextChannelById(logs.id);
            logsChannel.sendMessage(embedBuilder.build()).queue();

            message.getChannel().sendMessage(member.getAsMention()+" a été mute !").queue();

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
