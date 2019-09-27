package fr.gravendev.multibot.commands.commands.customs;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.CustomCommandDAO;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.data.CustomCommandData;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.api.entities.Message;

public class RemoveCommand implements CommandExecutor {
    private final CustomCommandDAO customCommandDAO;

    public RemoveCommand(DAOManager daoManager) {
        this.customCommandDAO = daoManager.getCustomCommandDAO();
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
            message.getChannel().sendMessage(Utils.errorArguments("custom remove", "<commande>")).queue();
            return;
        }

        customCommandDAO.delete(new CustomCommandData(args[0], ""));
        message.getChannel().sendMessage("La commande custom ``" + args[0] + "`` a été supprimée.").queue();

    }

}
