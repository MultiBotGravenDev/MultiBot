package fr.gravendev.multibot.commands.commands.customs;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.CustomCommandDAO;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.data.CustomCommandData;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Arrays;

public class SetCommand implements CommandExecutor {
    private final CustomCommandDAO customCommandDAO;

    public SetCommand(DAOManager daoManager) {
        this.customCommandDAO = daoManager.getCustomCommandDAO();
    }

    @Override
    public String getCommand() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "Permet de créer ou changer une commande custom.";
    }

    @Override
    public void execute(Message message, String[] args) {
        if (args.length < 2) {
            MessageEmbed setCommand = Utils.errorArguments("custom set", "<commande> <valeur>");
            message.getChannel().sendMessage(setCommand).queue();
            return;
        }

        String[] arrayWithoutFirstElement = Arrays.copyOfRange(args, 1, args.length);
        String joinWithSpace = String.join(" ", arrayWithoutFirstElement);
        CustomCommandData customCommandData = new CustomCommandData(args[0], joinWithSpace);

        customCommandDAO.save(customCommandData);
        message.getChannel().sendMessage("La commande ``" + args[0] + "`` a été enregistrée").queue();
    }
}
