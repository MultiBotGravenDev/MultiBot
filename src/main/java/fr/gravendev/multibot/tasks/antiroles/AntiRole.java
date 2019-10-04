package fr.gravendev.multibot.tasks.antiroles;

import fr.gravendev.multibot.database.dao.AntiRolesDAO;
import fr.gravendev.multibot.database.data.AntiRoleData;
import fr.gravendev.multibot.utils.Configuration;
import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class AntiRole {

    private final Guild guild;
    private final AntiRolesDAO antiRolesDAO;
    private final String roleName;
    private final Role role;

    //TODO: refactor to give AntiRolesDAO, not DAOManager
    AntiRole(Guild guild, AntiRolesDAO antiRolesDAO, Configuration role) {
        this.guild = guild;
        this.antiRolesDAO = antiRolesDAO;
        this.roleName = role.name().toLowerCase().replace("_", "-");
        this.role = guild.getRoleById(role.getValue());
    }

    public void deleteRoles() {

        List<Member> membersWithRole = this.guild.getMembersWithRoles(this.role);
        membersWithRole.forEach(this::computeRoleDeleting);

    }

    private void computeRoleDeleting(Member member) {

        AntiRoleData antiRoleData = this.antiRolesDAO.get(member.getUser().getId());

        if (mustRemoveRole(antiRoleData)) {

            removeRoleFromMember(member, antiRoleData);
            sendMessage(member);

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

    private void sendMessage(Member member) {
        String text = "Le rôle " + role.getName() + " vous a été retiré";
        member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(text).queue());
    }

    private void removeRoleFromMember(Member member, AntiRoleData antiRoleData) {
        GuildUtils.removeRole(member, this.role.getId()).queue();
        this.antiRolesDAO.delete(antiRoleData);
    }

}
