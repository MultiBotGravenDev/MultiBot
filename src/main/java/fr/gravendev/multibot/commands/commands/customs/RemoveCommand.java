package fr.gravendev.multibot.commands.commands.customs;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.CustomCommandDAO;
import fr.gravendev.multibot.database.data.CustomCommandData;
import net.dv8tion.jda.core.entities.Message;

public class RemoveCommand implements CommandExecutor {

    private final DatabaseConnection databaseConnection;

    public RemoveCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getCommand() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "Permet de supprimer un commande custom.";
    }

    @Override
    public void execute(Message message, String[] args) {

        if (args.length == 0) {
            message.getChannel().sendMessage("Erreur. !custom remove <commande>").queue();
            return;
        }

        new CustomCommandDAO(this.databaseConnection).delete(new CustomCommandData(args[0], ""));
        message.getChannel().sendMessage("La commande custom ``" + args[0] + "`` a été supprimée.").queue();

    }

}
