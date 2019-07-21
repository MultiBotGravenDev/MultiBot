package fr.gravendev.multibot.quiz.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.QuizMessageDAO;
import fr.gravendev.multibot.database.data.MessageData;
import fr.gravendev.multibot.quiz.WelcomeMessagesSetManager;
import net.dv8tion.jda.core.entities.Message;

import java.util.Arrays;
import java.util.List;

public class SetCommand implements CommandExecutor {

    private final QuizMessageDAO quizMessageDAO;
    private WelcomeMessagesSetManager welcomeMessagesSetManager;

    SetCommand(DatabaseConnection databaseConnection, WelcomeMessagesSetManager welcomeMessagesSetManager) {
        quizMessageDAO = new QuizMessageDAO(databaseConnection);
        this.welcomeMessagesSetManager = welcomeMessagesSetManager;
    }

    @Override
    public String getCommand() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "Permet de changer les questions du quizz et le message de bienvenue.";
    }

    @Override
    public List<String> getAuthorizedChannelsNames() {
        return Arrays.asList("lisez-ce-salon", "piliers");
    }

    @Override
    public void execute(Message message, String[] args) {

        if (args[0].equalsIgnoreCase("question")) {

            setQuestion(message, Arrays.copyOfRange(args, 1, args.length));

        } else if (args[0].equalsIgnoreCase("message")) {

            this.welcomeMessagesSetManager.onCommand(message);

        }

    }

    private void setQuestion(Message message, String[] args) {

        if (args.length > 2 || !args[0].matches("[0-9]+")) return;

        MessageData messageData = this.quizMessageDAO.get(args[0]);

        if (messageData == null) {

            int i = 1;

            while (this.quizMessageDAO.get(String.valueOf(i)) != null) {
                ++i;
            }

            messageData = new MessageData(String.valueOf(i), "");

        }

        args = Arrays.copyOfRange(args, 1, args.length);
        String question = String.join(" ", args);

        messageData = new MessageData(messageData.id, question);
        quizMessageDAO.save(messageData);

        message.getChannel().sendMessage("La question numéro " + messageData.id + " a bien été changée en :\n" + messageData.message).queue();

    }

}
