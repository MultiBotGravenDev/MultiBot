package fr.gravendev.multibot.system.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.AntiRolesDAO;
import fr.gravendev.multibot.database.dao.GuildIdDAO;

import fr.gravendev.multibot.database.data.AntiRoleData;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class AntiCommand implements CommandExecutor {

    private final DatabaseConnection databaseConnection;

    public AntiCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getCommand() {
        return "anti";
    }

    @Override
    public boolean isAuthorizedMember(Member member) {
        return member.hasPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.GUILD;
    }

    @Override
    public void execute(Message message, String[] args) {

        List<Member> mentionedMembers = message.getMentionedMembers();

        if (args.length == 0) return;
        if (mentionedMembers.size() != 1) return;
        if (!"repost review meme".contains(args[0])) return;

        try {
            GuildIdDAO guildIdDAO = new GuildIdDAO(this.databaseConnection);
            long roleId = guildIdDAO.get("anti_" + args[0]).id;

            Member member = mentionedMembers.get(0);
            message.getGuild().getController().addRolesToMember(member, message.getGuild().getRoleById(roleId)).queue(a -> {

                try {
                    AntiRolesDAO antiRolesDAO = new AntiRolesDAO(this.databaseConnection);
                    AntiRoleData antiRoleData = antiRolesDAO.get(member.getUser().getId());

                    if (antiRoleData == null) {
                        antiRoleData = new AntiRoleData(member.getUser().getIdLong(), new HashMap<>());
                    }

                    antiRoleData.roles.put(new Date(System.currentTimeMillis()), "anti-" + args[0]);

                    antiRolesDAO.save(antiRoleData);

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            });
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
