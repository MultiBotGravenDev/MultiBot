package fr.gravendev.multibot.utils;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

public class GuildUtils {

    public static boolean hasRole(Member member, String roleName) {
        Guild guild = member.getGuild();
        return guild.getRolesByName(roleName, false).stream()
                .findAny()
                .map(role -> guild.getMembersWithRoles(role).contains(member))
                .orElse(false);
    }

}
