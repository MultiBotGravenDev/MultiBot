package fr.gravendev.multibot.roles.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.RoleDAO;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.sql.SQLException;
import java.util.Objects;

public class HereCommand implements CommandExecutor {

    private final DatabaseConnection databaseConnection;

    public HereCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getCommand() {
        return "here";
    }

    @Override
    public void execute(Message message, String[] args) {

        MessageChannel channel = message.getChannel();
        channel.getHistoryBefore(message, 100).queue(messageHistory -> messageHistory.getRetrievedHistory().forEach(oldMessage -> oldMessage.delete().queue()));

        try {

            RoleDAO roleDAO = new RoleDAO(this.databaseConnection.getConnection());

            channel.sendMessage(roleDAO.get("message").emoteId)
                    .queue(sentMessage -> message.getGuild().getRoles().stream()
                            .map(role -> roleDAO.get(role.getId()))
                            .filter(Objects::nonNull)
                            .forEach(role -> sentMessage.addReaction(message.getGuild().getEmoteById(role.emoteId)).queue())
                    );

            message.delete().queue();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
