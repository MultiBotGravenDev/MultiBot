package fr.gravendev.multibot.events;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.AntiRolesDAO;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.database.data.AntiRoleData;
import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

class MemberJoinListener implements Listener<GuildMemberJoinEvent> {

    private final AntiRolesDAO antiRolesDAO;
    private final GuildIdDAO guildIdDAO;

    MemberJoinListener(DatabaseConnection databaseConnection) {
        this.antiRolesDAO = new AntiRolesDAO(databaseConnection);
        this.guildIdDAO = new GuildIdDAO(databaseConnection);
    }

    @Override
    public Class<GuildMemberJoinEvent> getEventClass() {
        return GuildMemberJoinEvent.class;
    }

    @Override
    public void executeListener(GuildMemberJoinEvent event) {

        Member member = event.getMember();

        AntiRoleData antiRoleData = this.antiRolesDAO.get(member.getId());
        antiRoleData.roles.forEach((date, roleName) -> GuildUtils.addRole(member, String.valueOf(this.guildIdDAO.get(roleName).id)).queue());

    }

}
