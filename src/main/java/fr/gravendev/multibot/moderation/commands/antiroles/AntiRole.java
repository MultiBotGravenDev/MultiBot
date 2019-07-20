package fr.gravendev.multibot.moderation.commands.antiroles;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.AntiRolesDAO;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.database.data.AntiRoleData;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.time.Instant;
import java.util.Date;

public abstract class AntiRole {

    private final AntiRolesDAO antiRolesDAO;
    private long roleId;

    AntiRole(DatabaseConnection databaseConnection, String roleName) {
        this.antiRolesDAO = new AntiRolesDAO(databaseConnection);
        this.roleId = new GuildIdDAO(databaseConnection).get(roleName).id;
    }

    public void deleteRoles(Guild guild) {

        Role role = guild.getRoleById(this.roleId);
        for (Member member : guild.getMembersWithRoles(role)) {

            AntiRoleData antiRoleData = this.antiRolesDAO.get(member.getUser().getId());

            boolean removeRole = antiRoleData.roles.entrySet().stream()
                    .filter(entry -> entry.getValue().contains("anti-repost"))
                    .anyMatch(entry -> entry.getKey().before(Date.from(Instant.now().minusSeconds(60 * 60 * 24 * 30 * 6))));

            if (removeRole) {
                guild.getController().removeRolesFromMember(member, role).queue();
                this.antiRolesDAO.delete(antiRoleData);
            }

        }

    }

}
