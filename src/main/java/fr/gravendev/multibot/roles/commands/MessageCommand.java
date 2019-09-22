package fr.gravendev.multibot.roles.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.RoleDAO;
import fr.gravendev.multibot.database.data.RoleData;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.Arrays;
import java.util.List;

public class MessageCommand implements CommandExecutor {

    private final RoleDAO roleDAO;

    MessageCommand(DAOManager daoManager) {
        this.roleDAO = daoManager.getRoleDAO();
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
        MessageChannel channel = message.getChannel();
        if(args.length == 0) {
            channel.sendMessage("Message: "+roleDAO.get("message").getEmoteId()).queue();
            return;
        }
        String roleMessage = String.join(" ", args);
        RoleData roleData = new RoleData("message", roleMessage);
        roleDAO.save(roleData);

        channel.sendMessage("Le message a bien été changé en :\n" + roleMessage).queue();
    }

}
