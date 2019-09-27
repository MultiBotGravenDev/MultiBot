package fr.gravendev.multibot.moderation.commands;

import fr.gravendev.multibot.commands.commands.CommandCategory;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.moderation.InfractionType;
import fr.gravendev.multibot.utils.Configuration;
import fr.gravendev.multibot.utils.GuildUtils;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;

import java.awt.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class UnmuteCommand implements CommandExecutor {

    private final InfractionDAO infractionDAO;

    public UnmuteCommand(DAOManager daoManager) {
        this.infractionDAO = daoManager.getInfractionDAO();
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
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    @Override
    public boolean isAuthorizedMember(Member member) {
        return member.hasPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(Message message, String[] args) {
        List<Member> mentionedMembers = message.getMentionedMembers();
        MessageChannel messageChannel = message.getChannel();
        Guild guild = message.getGuild();

        if (mentionedMembers.size() == 0) {
            message.getChannel().sendMessage(Utils.errorArguments(getCommand(), "@membre")).queue();
            return;
        }

        Member member = mentionedMembers.get(0);
        if (!GuildUtils.hasRole(member, "Muted")) {
            messageChannel.sendMessage(Utils.buildEmbed(Color.RED, "Ce membre n'est pas mute")).queue();
            return;
        }

        InfractionData data;
        try {
            data = infractionDAO.getLast(member.getUser().getId(), InfractionType.MUTE);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        if (data != null) {
            data.setEnd(new Date());
            data.setFinished(true);
            infractionDAO.save(data);
        }

        Role muted = guild.getRoleById(Configuration.MUTED.getValue());

        if (muted != null) {
            guild.removeRoleFromMember(member, muted).queue();
        }

        message.getChannel().sendMessage(Utils.buildEmbed(Color.DARK_GRAY, member.getUser().getAsTag() + " vient d'être unmute")).queue();
        member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Vous avez été unmute").queue());

    }
}
