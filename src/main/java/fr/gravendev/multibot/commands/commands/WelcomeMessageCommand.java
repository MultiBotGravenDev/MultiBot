package fr.gravendev.multibot.commands.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
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
            ResultSet resultSet = this.databaseConnection.getConnection().createStatement().executeQuery("SELECT * FROM welcome_messages");

            while (resultSet.next()) {
                message.getChannel().sendMessage(resultSet.getString("text")).queue();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        message.getChannel().sendMessage("").queue(rulesMessage -> rulesMessage.addReaction("\u2705").queue());
        message.delete().queue();

    }

}
