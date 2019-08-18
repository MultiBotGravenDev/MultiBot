package fr.gravendev.multibot.utils;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.requests.restaction.AuditableRestAction;

import java.util.List;

public class GuildUtils {

    public static boolean hasRole(Member member, String roleName) {
        Guild guild = member.getGuild();
        List<Role> memberRoles = guild.getRolesByName(roleName, false);
        return memberRoles.stream()
                .findAny()
                .map(role -> guild.getMembersWithRoles(role).contains(member))
                .orElse(false);
    }

    public static AuditableRestAction<Void> addRole(Member member, String roleId) {
        Guild guild = member.getGuild();
        Role roleToAdd = guild.getRoleById(roleId);

        GuildController guildController = guild.getController();
        return guildController.addSingleRoleToMember(member, roleToAdd);
    }

    public static AuditableRestAction<Void> removeRole(Member member, String roleId) {
        Guild guild = member.getGuild();
        Role roleToRemove = guild.getRoleById(roleId);

        GuildController guildController = guild.getController();
        return guildController.removeSingleRoleFromMember(member, roleToRemove);
    }

}
