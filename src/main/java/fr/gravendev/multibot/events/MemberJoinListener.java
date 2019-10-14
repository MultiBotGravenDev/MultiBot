package fr.gravendev.multibot.events;

import fr.gravendev.multibot.database.dao.AntiRolesDAO;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.data.AntiRoleData;
import fr.gravendev.multibot.utils.Configuration;
import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

class MemberJoinListener implements Listener<GuildMemberJoinEvent> {

    private final AntiRolesDAO antiRolesDAO;

    MemberJoinListener(DAOManager daoManager) {
        this.antiRolesDAO = daoManager.getAntiRolesDAO();
    }

    @Override
    public Class<GuildMemberJoinEvent> getEventClass() {
        return GuildMemberJoinEvent.class;
    }

    @Override
    public void executeListener(GuildMemberJoinEvent event) {
        Member member = event.getMember();
        String memberId = member.getId();
        AntiRoleData antiRoleData = antiRolesDAO.get(memberId);

        antiRoleData.getRoles().forEach((date, roleName) -> GuildUtils.addRole(member, Configuration.getConfigByName(roleName.replace("-","_")).getValue()).queue());
    }
}
