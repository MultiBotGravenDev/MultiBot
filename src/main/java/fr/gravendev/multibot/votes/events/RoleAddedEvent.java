package fr.gravendev.multibot.votes.events;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.VoteDAO;
import fr.gravendev.multibot.database.data.VoteData;
import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;

import java.util.Arrays;
import java.util.List;

public class RoleAddedEvent implements Listener<GuildMemberRoleAddEvent> {

    private static final List<String> ROLES_NAMES = Arrays.asList(
            "Honorable",
            "Développeur",
            "Pilier de la Commu");

    private final DatabaseConnection databaseConnection;

    public RoleAddedEvent(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Class<GuildMemberRoleAddEvent> getEventClass() {
        return GuildMemberRoleAddEvent.class;
    }

    @Override
    public void executeListener(GuildMemberRoleAddEvent event) {

        VoteDAO voteDAO = new VoteDAO(databaseConnection);
        VoteData voteData = voteDAO.get(event.getUser().getId());

        Role role = event.getRoles().get(0);

        Member member = event.getMember();
        if (GuildUtils.hasRole(member, "Pilier de la Commu") || GuildUtils.hasRole(member, "Gérant")) {
            return;
        }

        if (ROLES_NAMES.contains(role.getName()) && !voteData.accepted) {
            event.getGuild().getController().removeRolesFromMember(member, role).queue();
        }

    }

}
