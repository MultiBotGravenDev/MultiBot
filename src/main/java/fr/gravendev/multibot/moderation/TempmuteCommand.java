package fr.gravendev.multibot.moderation;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.dao.LogsDAO;
import fr.gravendev.multibot.database.data.GuildIdsData;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.logs.MessageData;
import fr.gravendev.multibot.utils.GuildUtils;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.utils.PermissionUtil;
import org.joda.time.Period;

import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TempmuteCommand implements CommandExecutor {

    private final DatabaseConnection databaseConnection;

    public TempmuteCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getCommand() {
        return "tempmute";
    }

    @Override
    public String getDescription() {
        return "Rendre temporairement muet un membre du discord.";
    }

    @Override
    public void execute(Message message, String[] args) {
        List<Member> mentionedMembers = message.getMentionedMembers();
        MessageChannel channel = message.getChannel();
        if (mentionedMembers.size() == 0 || args.length < 2) {
            channel.sendMessage(Utils.buildEmbed(Color.RED, "Utilisation: tempmute @membre durée")).queue();
            return;
        }

        Member member = mentionedMembers.get(0);
        User mutedUser = member.getUser();
        Period duration = Utils.getTimeFromInput(args[1]);
        if(duration == null) {
            channel.sendMessage(Utils.buildEmbed(Color.RED, "Durée invalide")).queue();
            return;
        }

        String reason = "Non définie";
        if (args.length >= 3) {
            reason = Stream.of(args).skip(2).collect(Collectors.joining(" "));
        }

        if(GuildUtils.hasRole(member, "muted")) {
            channel.sendMessage(Utils.buildEmbed(Color.RED, "Ce membre est déjà mute")).queue();
            return;
        }

        Guild guild = message.getGuild();
        Member bot = guild.getMember(message.getJDA().getSelfUser());
        if (!PermissionUtil.canInteract(bot, member)) {
            message.getChannel().sendMessage(Utils.buildEmbed(Color.RED, "Impossible de mute cet utilisateur !")).queue();
            return;
        }

        Calendar calendar = Calendar.getInstance();
        Date start = calendar.getTime();
        calendar.add(Calendar.SECOND, (int) duration.toStandardDuration().getStandardSeconds());
        Date end = calendar.getTime();

        InfractionDAO infractionDAO = new InfractionDAO(databaseConnection);
        InfractionData infractionData = new InfractionData(member.getUser().getId(), message.getAuthor().getId(), InfractionType.TEMPMUTE, reason, start, end);
        infractionDAO.save(infractionData);

        GuildIdDAO guildIdDAO = new GuildIdDAO(databaseConnection);
        long mutedID = guildIdDAO.get("muted").id;
        Role muted = guild.getRoleById(mutedID);

        LogsDAO logsDAO = new LogsDAO(databaseConnection);
        MessageData messageData = new MessageData(message);
        logsDAO.save(messageData);

        GuildIdsData logs = guildIdDAO.get("logs");

        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED)
                .setAuthor("[TEMPMUTE] " + mutedUser.getAsTag(), mutedUser.getAvatarUrl())
                .addField("Utilisateur:", mutedUser.getAsMention(), true)
                .addField("Modérateur:", message.getAuthor().getAsMention(), true)
                .addField("Raison:", reason, true)
                .addField("Jusqu'à:", Utils.getDateFormat().format(end), true);

        TextChannel logsChannel = guild.getTextChannelById(logs.id);
        logsChannel.sendMessage(embedBuilder.build()).queue();

        guild.getController().addRolesToMember(member, muted).queue();
        message.getChannel().sendMessage(Utils.muteEmbed(mutedUser, reason, end)).queue();
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
