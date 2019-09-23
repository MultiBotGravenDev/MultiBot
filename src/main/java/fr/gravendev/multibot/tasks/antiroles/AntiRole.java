package fr.gravendev.multibot.tasks.antiroles;

import fr.gravendev.multibot.database.dao.AntiRolesDAO;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.data.AntiRoleData;
import fr.gravendev.multibot.utils.Configuration;
import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.Calendar;
import java.util.Date;

public abstract class AntiRole {

    private final AntiRolesDAO antiRolesDAO;
    private final String roleName;
    private final String roleId;

    AntiRole(DAOManager daoManager, Configuration role) {
        this.antiRolesDAO = daoManager.getAntiRolesDAO();
        this.roleName = role.name().toLowerCase().replace("_", "-");
        this.roleId = role.getValue();
    }

    public void deleteRoles(Guild guild) {

        for (Member membersWithRole : guild.getMembersWithRoles(guild.getRoleById(this.roleId))) {
            this.computeRoleDeleting(membersWithRole);
        }

    }

    private void computeRoleDeleting(Member member) {

        Role role = member.getGuild().getRoleById(this.roleId);
        AntiRoleData antiRoleData = this.antiRolesDAO.get(member.getUser().getId());

        if (mustRemoveRole(antiRoleData) & role != null) {

            GuildUtils.removeRole(member, role.getId()).queue();
            this.antiRolesDAO.delete(antiRoleData);
            member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Le rôle " + role.getName() + " vous a été retiré").queue());

        }

    }

    private boolean mustRemoveRole(AntiRoleData antiRoleData) {
        return antiRoleData.getRoles().entrySet().stream()
                .peek(dateStringEntry -> dateStringEntry.setValue(dateStringEntry.getValue().toLowerCase()))
                .filter(entry -> entry.getValue().contains(this.roleName))
                .anyMatch(entry -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(entry.getKey());
                    calendar.add(Calendar.MONTH, 1);

                    Date now = new Date();

                    return now.after(calendar.getTime());
                });
    }

}
