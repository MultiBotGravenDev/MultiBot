package fr.gravendev.multibot.roles.listeners;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.RoleDAO;
import fr.gravendev.multibot.database.data.RoleData;
import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

public class ReactionAddedListener implements Listener<MessageReactionAddEvent> {

    private final RoleDAO roleDAO;

    public ReactionAddedListener(DatabaseConnection databaseConnection) {
        this.roleDAO = new RoleDAO(databaseConnection);
    }

    @Override
    public Class<MessageReactionAddEvent> getEventClass() {
        return MessageReactionAddEvent.class;
    }

    @Override
    public void executeListener(MessageReactionAddEvent event) {

        if (event.getUser().isBot() || !event.getChannel().getName().equalsIgnoreCase("rôle-langage")) return;

        RoleData roleData = roleDAO.get(event.getReactionEmote().getId());

        if (roleData != null) {
            GuildUtils.addRole(event.getMember(), roleData.roleId).queue();
        } else {
            event.getReaction().removeReaction(event.getUser()).queue();
        }

    }

}
