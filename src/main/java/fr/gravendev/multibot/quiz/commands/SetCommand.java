package fr.gravendev.multibot.quiz.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.QuizMessageDAO;
import fr.gravendev.multibot.database.data.MessageData;
import net.dv8tion.jda.core.entities.Message;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class SetCommand implements CommandExecutor {

    private final DatabaseConnection databaseConnection;

    SetCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getCommand() {
        return "set";
    }

    @Override
    public List<String> getAuthorizedChannelsNames() {
        return Arrays.asList("lisez-ce-salon", "piliers");
    }

    @Override
    public void execute(Message message, String[] args) {

        if (args.length > 2 || !args[0].matches("[0-9]+")) return;

        QuizMessageDAO quizMessageDAO = new QuizMessageDAO(this.databaseConnection);
        MessageData messageData = quizMessageDAO.get(args[0] + "");

        if (messageData == null) {

            int i = 1;

            while (quizMessageDAO.get(i + "") != null) {
                ++i;
            }

            messageData = new MessageData(i + "", "");

        }

        args = Arrays.copyOfRange(args, 1, args.length);
        String question = String.join(" ", args);

        messageData = new MessageData(messageData.id, question);
        quizMessageDAO.save(messageData);

        message.getChannel().sendMessage("La question numéro " + messageData.id + " a bien été changée en :\n" + messageData.message).queue();

    }

}
