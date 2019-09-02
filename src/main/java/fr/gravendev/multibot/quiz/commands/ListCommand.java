package fr.gravendev.multibot.quiz.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.QuizMessageDAO;
import fr.gravendev.multibot.database.data.MessageData;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

public class ListCommand implements CommandExecutor {

    private final QuizMessageDAO quizMessageDAO;

    ListCommand(DatabaseConnection databaseConnection) {
        this.quizMessageDAO = new QuizMessageDAO(databaseConnection);
    }

    @Override
    public String getCommand() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "Permet d'obtenir la liste des questions du quizz.";
    }

    @Override
    public void execute(Message message, String[] args) {
        buildMessage().sendTo(message.getChannel()).queue();
    }

    @NotNull
    private MessageBuilder buildMessage() {
        MessageBuilder messageBuilder = new MessageBuilder();
        MessageData messageData;

        for (int i = 0; (messageData = this.quizMessageDAO.get(String.valueOf(i))) != null; i++) {

            messageBuilder
                    .append(String.valueOf(i))
                    .append(" - ")
                    .append(messageData.message)
                    .append("\n");

        }

        return messageBuilder;
    }

}
