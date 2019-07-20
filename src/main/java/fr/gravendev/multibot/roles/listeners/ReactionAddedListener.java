package fr.gravendev.multibot.roles.listeners;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.RoleDAO;
import fr.gravendev.multibot.database.data.RoleData;
import fr.gravendev.multibot.events.Listener;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.sql.SQLException;

public class ReactionAddedListener implements Listener<MessageReactionAddEvent> {

    private final DatabaseConnection databaseConnection;

    public ReactionAddedListener(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Class<MessageReactionAddEvent> getEventClass() {
        return MessageReactionAddEvent.class;
    }

    @Override
    public void executeListener(MessageReactionAddEvent event) {

        User user = event.getUser();
        if (user.isBot()) return;
        if (!event.getChannel().getName().equalsIgnoreCase("rôle-langage")) return;

        Guild guild = event.getGuild();
        RoleDAO roleDAO = new RoleDAO(this.databaseConnection);
        RoleData roleData = roleDAO.get(event.getReactionEmote().getId());

        if (roleData != null) {
            guild.getController().addRolesToMember(event.getMember(), guild.getRoleById(roleData.roleId)).queue();
        } else {
            event.getReaction().removeReaction(user).queue();
        }

    }

}
