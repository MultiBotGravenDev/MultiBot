package fr.gravendev.multibot.roles.listeners;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.RoleDAO;
import fr.gravendev.multibot.database.data.RoleData;
import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

public class ReactionRemovedListener implements Listener<MessageReactionRemoveEvent> {

    private final RoleDAO roleDAO;

    public ReactionRemovedListener(DatabaseConnection databaseConnection) {
        this.roleDAO = new RoleDAO(databaseConnection);
    }

    @Override
    public Class<MessageReactionRemoveEvent> getEventClass() {
        return MessageReactionRemoveEvent.class;
    }

    @Override
    public void executeListener(MessageReactionRemoveEvent event) {

        if (!event.getChannel().getName().equalsIgnoreCase("rôle-langage")) return;

        RoleData roleData = roleDAO.get(event.getReactionEmote().getId());
        Member member = event.getMember();
        if (roleData != null && member != null) {
            GuildUtils.removeRole(event.getMember(), roleData.getRoleId()).queue();
        }

    }

}
