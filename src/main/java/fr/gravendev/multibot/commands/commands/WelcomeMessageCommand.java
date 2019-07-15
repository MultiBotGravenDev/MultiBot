package fr.gravendev.multibot.commands.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.CommandExecutor;
import fr.gravendev.multibot.data.WelcomeMessageData;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.WelcomeMessageDAO;
import fr.gravendev.multibot.json.FileLoader;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.awt.*;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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

            List<String> sentences = new ArrayList<>();

            WelcomeMessageDAO welcomeMessageDAO = new WelcomeMessageDAO(this.databaseConnection.getConnection());
            WelcomeMessageData welcomeMessageData;

            for (int i = 1; (welcomeMessageData = welcomeMessageDAO.get(i + "")) != null; ++i) {
                sentences.add(welcomeMessageData.message);
            }

            for (String sentence : sentences) {

                message.getChannel().sendMessage(sentence).queue(sentMessage -> {
                    if (sentences.indexOf(sentence) == sentences.size() - 1) {
                        sentMessage.addReaction("\u2705").queue();
                    }
                });

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        message.delete().queue();

    }

}
