package fr.gravendev.multibot.quiz.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.WelcomeMessageDAO;
import fr.gravendev.multibot.database.data.MessageData;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class HereCommand implements CommandExecutor {

    private final DatabaseConnection databaseConnection;

    HereCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getCommand() {
        return "here";
    }

    @Override
    public List<String> getAuthorizedChannelsNames() {
        return Collections.singletonList("lisez-ce-salon");
    }

    @Override
    public void execute(Message message, String[] args) {

        message.getChannel().getHistoryBefore(message, 100).queue(history -> history.getRetrievedHistory().forEach(message1 -> message1.delete().queue()));

        try {

            WelcomeMessageDAO welcomeMessageDAO = new WelcomeMessageDAO(this.databaseConnection.getConnection());
            MessageData messageData;

            MessageBuilder messageBuilder = new MessageBuilder();

            for (int i = 1; (messageData = welcomeMessageDAO.get(i + "")) != null; ++i) {
                messageBuilder.append(messageData.message);
            }

            Queue<Message> queue = messageBuilder.buildAll(MessageBuilder.SplitPolicy.NEWLINE);
            List<Message> messages = Arrays.stream(queue.toArray(new Message[]{})).collect(Collectors.toList());

            for (int i = 0; i < messages.size() - 1; i++) {
                message.getChannel().sendMessage(messages.get(i)).queue();
            }

            message.getChannel().sendMessage(messages.get(messages.size() - 1)).queue(sentMessage -> sentMessage.addReaction("\u2705").queue());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        message.delete().queue();

    }

}
