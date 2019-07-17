package fr.gravendev.multibot.quiz;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.data.MessageData;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.WelcomeMessageDAO;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class WelcomeMessageCommand implements CommandExecutor {

    private final DatabaseConnection databaseConnection;

    public WelcomeMessageCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getCommand() {
        return "welcome";
    }

    @Override
    public String getDescription() {
        return "envoie le message de bienvenue et la réaction pour recevoir le formulaire";
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.GUILD;
    }

    @Override
    public List<String> getAuthorizedChannelsNames() {
        return Collections.singletonList("lisez-ce-salon");
    }

    @Override
    public boolean isAuthorizedMember(Member member) {
        return member.hasPermission(Permission.ADMINISTRATOR);
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