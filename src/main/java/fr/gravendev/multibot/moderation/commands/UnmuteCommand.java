package fr.gravendev.multibot.moderation.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.moderation.InfractionType;
import fr.gravendev.multibot.utils.GuildUtils;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class UnmuteCommand implements CommandExecutor {

    private DatabaseConnection databaseConnection;
    public UnmuteCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

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
        Guild guild = message.getGuild();

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
            data.setEnd(new Date());
            infractionDAO.save(data);
        }

        GuildIdDAO guildIdDAO = new GuildIdDAO(databaseConnection);
        long mutedID = guildIdDAO.get("muted").id;
        Role muted = guild.getRoleById(mutedID);

        guild.getController().removeSingleRoleFromMember(member, muted).queue();
        message.getChannel().sendMessage(Utils.buildEmbed(Color.DARK_GRAY, member.getUser().getAsTag()+" vient d'être unmute")).queue();

    }
}
