package fr.gravendev.multibot.roles.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.RoleDAO;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HereCommand implements CommandExecutor {

    private final RoleDAO roleDAO;

    HereCommand(DatabaseConnection databaseConnection) {
        this.roleDAO = new RoleDAO(databaseConnection);
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

        Guild guild = message.getGuild();
        channel.sendMessage(roleDAO.get("message").getEmoteId())
                .queue(sentMessage -> guild.getRoles().stream()
                        .map(role -> roleDAO.get(role.getId()))
                        .filter(Objects::nonNull)
                        .forEach(role -> sentMessage.addReaction(guild.getEmoteById(role.getEmoteId())).queue())
                );

        message.delete().queue();

    }

}
