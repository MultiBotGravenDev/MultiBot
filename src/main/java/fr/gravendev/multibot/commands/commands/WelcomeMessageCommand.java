package fr.gravendev.multibot.commands.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.CommandExecutor;
import fr.gravendev.multibot.data.WelcomeMessageData;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.WelcomeMessageDAO;
import net.dv8tion.jda.core.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WelcomeMessageCommand extends CommandExecutor {

    private final DatabaseConnection databaseConnection;

    public WelcomeMessageCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public String getCommand() {
        return "welcome";
    }

    public String getDescription() {
        return "envoie le message de bienvenue et la réaction pour recevoir le formulaire";
    }

    public ChannelType getChannelType() {
        return ChannelType.GUILD;
    }

    public List<String> getAuthorizedChannelsNames() {
        return Collections.singletonList("lisez-ce-salon");
    }

    public void execute(Message message, String[] args) {

        try {

            List<String> sentences = loadSentences();

            for (int i = 0; i < sentences.size() - 1; i++) {
                message.getChannel().sendMessage(sentences.get(i)).queue();
            }

            message.getChannel().sendMessage(sentences.get(sentences.size() - 1)).queue(sentMessage -> sentMessage.addReaction("\u2705").queue());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        message.delete().queue();

    }

    private List<String> loadSentences() throws SQLException {
        List<String> sentences = new ArrayList<>();

        WelcomeMessageDAO welcomeMessageDAO = new WelcomeMessageDAO(this.databaseConnection.getConnection());
        WelcomeMessageData welcomeMessageData;

        for (int i = 1; (welcomeMessageData = welcomeMessageDAO.get(i + "")) != null; ++i) {
            sentences.add(welcomeMessageData.message);
        }

        return sentences;
    }

}
