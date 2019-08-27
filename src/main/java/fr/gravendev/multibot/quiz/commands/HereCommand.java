package fr.gravendev.multibot.quiz.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.WelcomeMessageDAO;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

public class HereCommand implements CommandExecutor {

    private final WelcomeMessageDAO welcomeMessageDAO;

    HereCommand(DatabaseConnection databaseConnection) {
        this.welcomeMessageDAO = new WelcomeMessageDAO(databaseConnection);
    }

    @Override
    public String getCommand() {
        return "here";
    }

    @Override
    public String getDescription() {
        return "Permet d'envoyer le message de bienvenue et la réactions permettant de déclencher le quizz.";
    }

    @Override
    public List<String> getAuthorizedChannelsNames() {
        return Collections.singletonList("lisez-ce-salon");
    }

    @Override
    public void execute(Message message, String[] args) {
        deleteMessages(message);
        sendMessages(message);
    }

    private void sendMessages(Message message) {
        MessageChannel channel = message.getChannel();

        Queue<Message> builtMessages = new MessageBuilder()
                .append(this.welcomeMessageDAO.get(String.valueOf(1)).message)
                .buildAll(MessageBuilder.SplitPolicy.NEWLINE);

        List<Message> messages = new ArrayList<>(builtMessages);

        for (int i = 0; i < messages.size() - 1; i++) {
            channel.sendMessage(messages.get(i)).queue();
        }

        channel.sendMessage(messages.get(messages.size() - 1)).queue(sentMessage -> sentMessage.addReaction("\u2705").queue());
    }

    private void deleteMessages(Message message) {
        message.getChannel().getHistoryBefore(message, 100).queue(history -> history.getRetrievedHistory().forEach(message1 -> message1.delete().queue()));
        message.delete().queue();
    }

}
