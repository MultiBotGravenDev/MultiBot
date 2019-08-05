package fr.gravendev.multibot.utils;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.requests.restaction.AuditableRestAction;

public class GuildUtils {

    public static boolean hasRole(Member member, String roleName) {
        Guild guild = member.getGuild();
        return guild.getRolesByName(roleName, false).stream()
                .findAny()
                .map(role -> guild.getMembersWithRoles(role).contains(member))
                .orElse(false);
    }

    public static AuditableRestAction<Void> addRole(Member member, String roleId) {
        return member.getGuild().getController().addSingleRoleToMember(member, member.getGuild().getRoleById(roleId));
    }

    public static AuditableRestAction<Void> removeRole(Member member, String roleId) {
        return member.getGuild().getController().removeSingleRoleFromMember(member, member.getGuild().getRoleById(roleId));
    }

}
