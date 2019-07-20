package fr.gravendev.multibot.commands.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.CustomCommandDAO;
import fr.gravendev.multibot.database.data.CustomCommandData;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.SelfUser;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class HelpCommand implements CommandExecutor {

    private final List<CommandExecutor> commandExecutors;
    private final DatabaseConnection databaseConnection;

    public HelpCommand(List<CommandExecutor> commandExecutors, DatabaseConnection databaseConnection) {
        this.commandExecutors = commandExecutors;
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getCommand() {
        return "help";
    }

    @Override
    public void execute(Message message, String[] args) {

        SelfUser bot = message.getJDA().getSelfUser();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.BLUE)
                .setTitle("Liste des commandes")
                .setAuthor(bot.getName(), bot.getAvatarUrl(), bot.getAvatarUrl());

        this.commandExecutors.stream()
                .filter(commandExecutor -> commandExecutor.isAuthorizedMember(message.getMember()))
                .forEach(commandExecutor -> embedBuilder.addField(new MessageEmbed.Field(commandExecutor.getCommand(), commandExecutor.getDescription(), false)));

        try {
            CustomCommandDAO customCommandDAO = new CustomCommandDAO(this.databaseConnection);

            CustomCommandData customCommandData;

            for (int i = 0; i < 100; i++) {
                customCommandData = customCommandDAO.get(String.valueOf(i));

                if (customCommandData != null) {
                    embedBuilder.addField(customCommandData.command, "", false);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        message.getChannel().sendMessage(embedBuilder.build()).queue();

    }

}
