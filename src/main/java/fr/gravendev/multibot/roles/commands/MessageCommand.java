package fr.gravendev.multibot.roles.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.RoleDAO;
import fr.gravendev.multibot.database.data.RoleData;
import net.dv8tion.jda.core.entities.Message;

import java.util.Arrays;
import java.util.List;

public class MessageCommand implements CommandExecutor {

    private final RoleDAO roleDAO;

    MessageCommand(DatabaseConnection databaseConnection) {
        this.roleDAO = new RoleDAO(databaseConnection);
    }

    @Override
    public String getCommand() {
        return "message";
    }

    @Override
    public String getDescription() {
        return "Permet de changer le message.";
    }

    @Override
    public List<String> getAuthorizedChannelsNames() {
        return Arrays.asList("rôle-langage", "piliers");
    }

    @Override
    public void execute(Message message, String[] args) {

        this.roleDAO.save(new RoleData("message", String.join(" ", args)));
        message.getChannel().sendMessage("Le message a bien été changé en :\n" + String.join(" ", args)).queue();

    }

}
