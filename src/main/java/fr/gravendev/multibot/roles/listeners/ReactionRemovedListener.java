package fr.gravendev.multibot.roles.listeners;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.RoleDAO;
import fr.gravendev.multibot.database.data.RoleData;
import fr.gravendev.multibot.events.Listener;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;

import java.sql.SQLException;

public class ReactionRemovedListener implements Listener<MessageReactionRemoveEvent> {

    private final DatabaseConnection databaseConnection;

    public ReactionRemovedListener(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Class<MessageReactionRemoveEvent> getEventClass() {
        return MessageReactionRemoveEvent.class;
    }

    @Override
    public void executeListener(MessageReactionRemoveEvent event) {

        if (!event.getChannel().getName().equalsIgnoreCase("rôle-langage")) return;

        RoleDAO roleDAO = new RoleDAO(this.databaseConnection);
        RoleData roleData = roleDAO.get(event.getReactionEmote().getId());

        if (roleData != null) {
            Guild guild = event.getGuild();
            guild.getController().removeRolesFromMember(event.getMember(), guild.getRoleById(roleData.roleId)).queue();
        }

    }

}
