package fr.gravendev.multibot.roles.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.RoleDAO;
import fr.gravendev.multibot.database.data.RoleData;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HereCommand implements CommandExecutor {

    private final RoleDAO roleDAO;

    HereCommand(DAOManager daoManager) {
        this.roleDAO = daoManager.getRoleDAO();
    }

    @Override
    public String getCommand() {
        return "here";
    }

    @Override
    public String getDescription() {
        return "Permet d'envoyer le message et les réactions pour obtenir les rôles.";
    }

    @Override
    public List<String> getAuthorizedChannelsNames() {
        return Collections.singletonList("rôle-langage");
    }

    @Override
    public void execute(Message message, String[] args) {

        MessageChannel channel = message.getChannel();
        channel.getHistoryBefore(message, 100).queue(messageHistory -> messageHistory.getRetrievedHistory().forEach(oldMessage -> oldMessage.delete().queue()));

        String roleMessage = roleDAO.get("message").getEmoteId();
        channel.sendMessage(roleMessage).queue(this::addEmotes);
        message.delete().queue();
    }

    private void addEmotes(Message message) {

        Guild guild = message.getGuild();

        guild.getRoles().stream()
                .map(Role::getId)
                .map(roleDAO::get)
                .filter(Objects::nonNull)
                .map(RoleData::getEmoteId)
                .map(guild::getEmoteById)
                .filter(Objects::nonNull)
                .map(message::addReaction)
                .forEach(RestAction::queue);

    }

}
