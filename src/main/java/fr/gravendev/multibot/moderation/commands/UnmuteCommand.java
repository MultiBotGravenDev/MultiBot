package fr.gravendev.multibot.moderation;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.utils.GuildUtils;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UnmuteCommand implements CommandExecutor {
    @Override
    public String getCommand() {
        return "unmute";
    }

    @Override
    public String getDescription() {
        return "Rendre la parole à un membre";
    }

    @Override
    public void execute(Message message, String[] args) {
        List<Member> mentionedMembers = message.getMentionedMembers();
        MessageChannel messageChannel = message.getChannel();
        if (mentionedMembers.size() == 0) {
            messageChannel.sendMessage(Utils.buildEmbed(Color.RED, "Utilisation: unmute @membre")).queue();
            return;
        }

        Member member = mentionedMembers.get(0);
        if(!GuildUtils.hasRole(member, "Muted")) {
            message.getChannel().sendMessage(Utils.buildEmbed(Color.RED, "Ce membre n'est pas mute")).queue();
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

        if(data != null) {
            messageChannel.sendMessage(Utils.buildEmbed(Color.RED, "Impossible de trouver des informations sur le mute de ce membre")).queue();
            return;
        }

    }
}
