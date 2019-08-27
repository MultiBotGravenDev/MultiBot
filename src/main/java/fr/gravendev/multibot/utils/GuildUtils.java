package fr.gravendev.multibot.utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

import java.util.List;
import java.util.Objects;

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

        return guild.addRoleToMember(member, Objects.requireNonNull(roleToAdd));
    }

    public static AuditableRestAction<Void> removeRole(Member member, String roleId) {
        Guild guild = member.getGuild();
        Role roleToRemove = guild.getRoleById(roleId);
        return guild.removeRoleFromMember(member, Objects.requireNonNull(roleToRemove));
    }

}
