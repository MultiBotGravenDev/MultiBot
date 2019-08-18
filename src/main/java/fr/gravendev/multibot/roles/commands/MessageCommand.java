package fr.gravendev.multibot.roles.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.RoleDAO;
import fr.gravendev.multibot.database.data.RoleData;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

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
        return Arrays.asList("rôle-langage-test", "piliers");
    }

    @Override
    public void execute(Message message, String[] args) {
        // TODO Talk with Luka / Nolan to determine what it is (and so rename inlinedArgs to something else)
        String inlinedArgs = String.join(" ", args);
        RoleData roleData = new RoleData("message", inlinedArgs);
        roleDAO.save(roleData);

        MessageChannel channel = message.getChannel();
        channel.sendMessage("Le message a bien été changé en :\n" + inlinedArgs).queue();
    }

}
