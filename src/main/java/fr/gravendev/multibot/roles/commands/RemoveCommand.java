package fr.gravendev.multibot.roles.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.RoleDAO;
import fr.gravendev.multibot.database.data.RoleData;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;

import java.util.Arrays;
import java.util.List;

public class RemoveCommand implements CommandExecutor {

    private final RoleDAO roleDAO;

    RemoveCommand(DatabaseConnection databaseConnection) {
        this.roleDAO = new RoleDAO(databaseConnection);
    }

    @Override
    public String getCommand() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "Permet de supprimer un rôle.";
    }

    @Override
    public List<String> getAuthorizedChannelsNames() {
        return Arrays.asList("rôle-langage-test", "piliers");
    }

    @Override
    public void execute(Message message, String[] args) {

        List<Role> mentionedRoles = message.getMentionedRoles();
        MessageChannel channel = message.getChannel();
        if (mentionedRoles.size() != 1) {
            channel.sendMessage("Erreur. !roles remove @role").queue();
            return;
        }

        Role mentionedRole = mentionedRoles.get(0);
        String mentionedRoleId = mentionedRole.getId();
        RoleData roleData = roleDAO.get(mentionedRoleId);

        if (roleData != null) {
            roleDAO.delete(roleData);
            channel.sendMessage("Le rôle " + mentionedRole.getAsMention() + " a bien été supprimé de la liste des rôles").queue();
            return;
        }
        channel.sendMessage("Ce rôle n'existe pas").queue();
    }

}
