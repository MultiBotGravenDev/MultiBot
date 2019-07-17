package fr.gravendev.multibot.roles.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.RoleDAO;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

import java.sql.SQLException;
import java.util.List;

public class RemoveCommand implements CommandExecutor {

    private final DatabaseConnection databaseConnection;

    RemoveCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getCommand() {
        return "remove";
    }

    @Override
    public void execute(Message message, String[] args) {

        List<Role> mentionedRoles = message.getMentionedRoles();

        if (args.length != 1) return;
        if (mentionedRoles.size() != 1) return;

        try {
            RoleDAO roleDAO = new RoleDAO(this.databaseConnection.getConnection());

            Role mentionedRole = mentionedRoles.get(0);

            if (roleDAO.get(mentionedRole.getId()) != null) {

                roleDAO.delete(roleDAO.get(mentionedRole.getId()));
                message.getChannel().sendMessage("Le role "
                        + mentionedRole.getAsMention()
                        + " a bien été supprimé à la liste des rôles").queue();
            } else {
                message.getChannel().sendMessage("Ce role n'existe pas").queue();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
