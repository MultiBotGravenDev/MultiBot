package fr.gravendev.multibot.commands.commands.customs;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.CustomCommandDAO;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.data.CustomCommandData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.awt.*;
import java.util.Collection;

public class ListCommand implements CommandExecutor {

    private static final int COMMAND_DESCRIPTION_MAX_LENGTH = 50;
    
    private CustomCommandDAO customCommandDAO;
    
    public ListCommand(DAOManager daoManager) {
        this.customCommandDAO = daoManager.getCustomCommandDAO();
    }
    
    @Override
    public String getCommand() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "Affiche la liste des commandes customs.";
    }

    @Override
    public void execute(Message message, String[] args) {
        MessageChannel channel = message.getChannel();
        
        EmbedBuilder currentEmbedBuilder = new EmbedBuilder();
        currentEmbedBuilder.setTitle("Commandes Customs");
        currentEmbedBuilder.setColor(Color.MAGENTA);
        
        Collection<CustomCommandData> commands = this.customCommandDAO.getAll();
        
        if (commands.isEmpty()) {
            currentEmbedBuilder.setDescription(":no_entry_sign: Aucune commande custom disponible !");
        }
        
        for(CustomCommandData command : commands) {
            String commandText = this.buildCommandText(command);
            
            try {
                currentEmbedBuilder.appendDescription(commandText);
            } catch(IllegalArgumentException e) {
                channel.sendMessage(currentEmbedBuilder.build()).queue();

                currentEmbedBuilder.setDescription(commandText);
                currentEmbedBuilder.setColor(Color.MAGENTA);
            }
        }
        
        channel.sendMessage(currentEmbedBuilder.build()).queue();
    }
    
    private String buildCommandText(CustomCommandData command) {
        String clippedText = command.getText();

        if (clippedText.length() >= COMMAND_DESCRIPTION_MAX_LENGTH) {
            clippedText = clippedText.substring(0, COMMAND_DESCRIPTION_MAX_LENGTH).trim() + "...";
        }

        return ":label: **`" + command.getCommand() + "`**\n" + 
                clippedText + "\n\n";
    }
    
}
