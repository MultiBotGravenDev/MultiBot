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
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WarnCommand implements CommandExecutor {

    private final DatabaseConnection databaseConnection;

    public WarnCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getCommand() {
        return "warn";
    }

    @Override
    public String getDescription() {
        return "Mettre un avertissement à un membre du discord.";
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.GUILD;
    }

    @Override
    public void execute(Message message, String[] args) {

        List<Member> mentionedMembers = message.getMentionedMembers();
        if (mentionedMembers.size() == 0) {
            message.getChannel().sendMessage("Usage: warn @Member").queue();
            return;
        }

        Guild guild = message.getGuild();

        Member member = mentionedMembers.get(0);
        Member bot = guild.getMember(message.getJDA().getSelfUser());

        User moderator = message.getAuthor();
        User warnedUser = member.getUser();

        String reason = "Non définie";
        if (args.length >= 2) {
            reason = Stream.of(args).skip(1).collect(Collectors.joining(" "));
        }

        if (!PermissionUtil.canInteract(bot, member)) {
            message.getChannel().sendMessage(Utils.buildEmbed(Color.RED, "Impossible de mettre un avertissement à cet utilisateur !")).queue();
            return;
        }

        InfractionData data = new InfractionData(
                warnedUser.getId(), moderator.getId(), InfractionType.WARN, reason, new Date(), null);
        InfractionDAO dao = new InfractionDAO(databaseConnection);
        dao.save(data);


        LogsDAO logsDAO = new LogsDAO(databaseConnection);
        MessageData messageData = new MessageData(message);
        logsDAO.save(messageData);

        GuildIdDAO guildIdDAO = new GuildIdDAO(databaseConnection);
        GuildIdsData logs = guildIdDAO.get("logs");

        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED)
                .setAuthor("[WARN] " + warnedUser.getAsTag(), warnedUser.getAvatarUrl())
                .addField("Utilisateur:", warnedUser.getAsMention(), true)
                .addField("Modérateur:", moderator.getAsMention(), true)
                .addField("Raison:", reason, true);

        TextChannel logsChannel = guild.getTextChannelById(logs.id);
        logsChannel.sendMessage(embedBuilder.build()).queue();

        message.getChannel().sendMessage(Utils.muteEmbed(warnedUser, reason)).queue();
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