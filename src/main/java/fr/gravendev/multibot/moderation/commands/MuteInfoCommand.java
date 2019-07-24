package fr.gravendev.multibot.moderation.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.moderation.InfractionType;
import fr.gravendev.multibot.utils.GuildUtils;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.awt.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class MuteInfoCommand implements CommandExecutor {

    private final DatabaseConnection databaseConnection;

    public MuteInfoCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getCommand() {
        return "muteinfo";
    }

    @Override
    public String getDescription() {
        return "Obtenir des informations sur le mute d'un membre.";
    }

    @Override
    public boolean isAuthorizedMember(Member member) {
        return member.hasPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(Message message, String[] args) {
        List<Member> mentionedMembers = message.getMentionedMembers();
        MessageChannel messageChannel = message.getChannel();
        if (mentionedMembers.size() == 0) {
            messageChannel.sendMessage(Utils.buildEmbed(Color.RED, "Utilisation: muteinfo @member")).queue();
            return;
        }

        Member member = mentionedMembers.get(0);
        if (!GuildUtils.hasRole(member, "Muted")) {
            messageChannel.sendMessage(Utils.buildEmbed(Color.RED, "Ce membre n'est pas mute")).queue();
            return;
        }

        InfractionDAO infractionDAO = new InfractionDAO(databaseConnection);
        InfractionData data;
        try {
            data = infractionDAO.getLast(member.getUser().getId(), InfractionType.MUTE);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        if (data == null) {
            messageChannel.sendMessage(Utils.buildEmbed(Color.RED, "Impossible de trouver des informations sur le mute de ce membre")).queue();
            return;
        }

        Date dateEnd = data.getEnd();
        String end = dateEnd == null ? "Jamais" : Utils.getDateFormat().format(dateEnd);

        EmbedBuilder embed = new EmbedBuilder().setColor(Color.RED)
                .setTitle("Informations de mute " + member.getUser().getName())
                .addField("Raison:", data.getReason(), false)
                .addField("Date de fin:", end, false)
                .addField("Par:", "<@" + data.getPunisher_id() + ">", false)
                .addField("Le:", Utils.getDateFormat().format(data.getStart()), false);

        messageChannel.sendMessage(embed.build()).queue();

    }
}
