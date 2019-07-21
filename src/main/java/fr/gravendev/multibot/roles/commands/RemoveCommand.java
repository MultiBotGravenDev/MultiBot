package fr.gravendev.multibot.roles.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.RoleDAO;
import net.dv8tion.jda.core.entities.Message;
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
        return Arrays.asList("rôle-langage", "piliers");
    }

    @Override
    public void execute(Message message, String[] args) {

        List<Role> mentionedRoles = message.getMentionedRoles();
        if (mentionedRoles.size() != 1) {
            message.getChannel().sendMessage("Erreur. !roles remove @role").queue();
            return;
        }

        Role mentionedRole = mentionedRoles.get(0);

        if (roleDAO.get(mentionedRole.getId()) != null) {

            roleDAO.delete(roleDAO.get(mentionedRole.getId()));
            message.getChannel().sendMessage("Le role "
                    + mentionedRole.getAsMention()
                    + " a bien été supprimé à la liste des rôles").queue();
        } else {
            message.getChannel().sendMessage("Ce role n'existe pas").queue();
        }

    }

}
