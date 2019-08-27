package fr.gravendev.multibot.commands.commands.customs;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.CustomCommandDAO;
import fr.gravendev.multibot.database.data.CustomCommandData;
import net.dv8tion.jda.api.entities.Message;

import java.util.Arrays;

public class SetCommand implements CommandExecutor {

    private final CustomCommandDAO CustomCommandDAO;

    public SetCommand(DatabaseConnection databaseConnection) {
        this.CustomCommandDAO = new CustomCommandDAO(databaseConnection);
    }

    @Override
    public String getCommand() {
        return "setChoice";
    }

    @Override
    public String getDescription() {
        return "Permet de créer ou changer une commande custom.";
    }

    @Override
    public void execute(Message message, String[] args) {

        if (args.length < 2) {
            message.getChannel().sendMessage("Erreur. !custom setChoice <commande> <texte>").queue();
            return;
        }

        CustomCommandData customCommandData = new CustomCommandData(args[0], String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
        this.CustomCommandDAO.save(customCommandData);
        message.getChannel().sendMessage("La commande ``" + args[0] + "`` a été enregistrée").queue();

    }

}
