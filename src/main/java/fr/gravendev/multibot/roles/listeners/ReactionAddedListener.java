package fr.gravendev.multibot.roles.listeners;

import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.RoleDAO;
import fr.gravendev.multibot.database.data.RoleData;
import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class ReactionAddedListener implements Listener<MessageReactionAddEvent> {

    private final RoleDAO roleDAO;

    public ReactionAddedListener(DAOManager daoManager) {
        this.roleDAO = daoManager.getRoleDAO();
    }

    @Override
    public Class<MessageReactionAddEvent> getEventClass() {
        return MessageReactionAddEvent.class;
    }

    @Override
    public void executeListener(MessageReactionAddEvent event) {
        if (event.getUser().isBot() || !event.getChannel().getName().equalsIgnoreCase("rôle-langage")) return;

        RoleData roleData = roleDAO.get(event.getReactionEmote().getId());

        Member member = event.getMember();
        if (roleData != null && member != null) {
            GuildUtils.addRole(member, roleData.getRoleId()).queue();
        } else {
            event.getReaction().removeReaction(event.getUser()).queue();
        }

    }

}
