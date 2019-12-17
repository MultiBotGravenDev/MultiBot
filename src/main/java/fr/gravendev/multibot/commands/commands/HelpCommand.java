package fr.gravendev.multibot.commands.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.database.dao.CustomCommandDAO;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.data.CustomCommandData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HelpCommand implements CommandExecutor {
    private final List<CommandExecutor> commandExecutors;
    private final CustomCommandDAO customCommandDAO;

    public HelpCommand(List<CommandExecutor> commandExecutors, DAOManager daoManager) {
        this.commandExecutors = commandExecutors;
        this.customCommandDAO = daoManager.getCustomCommandDAO();
    }

    @Override
    public String getCommand() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Commande d'aide regroupant toutes les commandes.";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.UTILS;
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.GUILD;
    }

    @Override
    public List<String> getAuthorizedChannelsNames() {
        return Collections.singletonList("commandes");
    }

    @Override
    public void execute(Message message, String[] args) {
        List<MessageEmbed> embeds = new ArrayList<>();

        for (CommandCategory category : CommandCategory.values()) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setColor(category.getColor())
                    .setTitle(category.getName());
            this.commandExecutors.stream()
                    .filter(command -> command.getCategory() == category && command.getChannelType().equalsTo(message.getChannelType()) && command.isAuthorizedMember(message.getMember()))
                    .forEach(command -> embedBuilder.addField(command.getCommand(), command.getDescription(), false));
            embeds.add(embedBuilder.build());
        }

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.MAGENTA)
                .setTitle("Commandes personnalisées");
        getCustomCommands().forEach(customCommand -> embedBuilder.addField(customCommand.getCommand(), "", true));
        embeds.add(embedBuilder.build());

        for (MessageEmbed embed : embeds) {
            if (embed.getFields().size() == 0) {
                continue;
            }
            message.getChannel().sendMessage(embed).queue();
        }
    }

    private List<CustomCommandData> getCustomCommands() {
        List<CustomCommandData> customCommands = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            CustomCommandData customCommandData = customCommandDAO.get(String.valueOf(i));
            if (customCommandData != null) {
                customCommands.add(customCommandData);
            }
        }
        return customCommands;
    }

}
