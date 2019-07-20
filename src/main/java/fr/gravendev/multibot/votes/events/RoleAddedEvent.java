package fr.gravendev.multibot.votes.events;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.VoteDAO;
import fr.gravendev.multibot.database.data.VoteData;
import fr.gravendev.multibot.events.Listener;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class RoleAddedEvent implements Listener<GuildMemberRoleAddEvent> {

    private static final List<String> ROLES_NAMES = Arrays.asList("Honorable", "Développeur", "Pilier de la Commu");

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

        VoteDAO voteDAO = new VoteDAO(this.databaseConnection);
        VoteData voteData = voteDAO.get(event.getUser().getId());

        Role role = event.getRoles().get(0);
        if (ROLES_NAMES.contains(role.getName()) && !voteData.accepted) {
            event.getGuild().getController().removeRolesFromMember(event.getMember(), role).queue();
        }

    }

}
