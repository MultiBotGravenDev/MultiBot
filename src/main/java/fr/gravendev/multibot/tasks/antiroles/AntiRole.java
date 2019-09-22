package fr.gravendev.multibot.tasks.antiroles;

import fr.gravendev.multibot.database.dao.AntiRolesDAO;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.data.AntiRoleData;
import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.time.Instant;
import java.util.Date;

public abstract class AntiRole {

    private final AntiRolesDAO antiRolesDAO;
    private final String roleName;
    private final long roleId;

    AntiRole(DAOManager daoManager, String roleName) {
        this.antiRolesDAO = daoManager.getAntiRolesDAO();
        this.roleName = roleName;
        this.roleId = daoManager.getGuildIdDAO().get(roleName).id;
    }

    public void deleteRoles(Guild guild) {

        guild.getMembersWithRoles(guild.getRoleById(this.roleId)).forEach(this::computeRoleDeleting);

    }

    private void computeRoleDeleting(Member member) {

        Role role = member.getGuild().getRoleById(this.roleId);
        AntiRoleData antiRoleData = this.antiRolesDAO.get(member.getUser().getId());

        if (mustRemoveRole(antiRoleData) & role != null) {

            GuildUtils.removeRole(member, role.getName());
            this.antiRolesDAO.delete(antiRoleData);
            member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Le rôle " + role.getName() + " vous a été retiré").queue());

        }

    }

    private boolean mustRemoveRole(AntiRoleData antiRoleData) {
        return antiRoleData.getRoles().entrySet().stream()
                .filter(entry -> entry.getValue().contains(this.roleName))
                .anyMatch(entry -> entry.getKey().before(Date.from(Instant.now().minusSeconds(60 * 60 * 24 * 30 * 6))));
    }

}
