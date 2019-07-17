package fr.gravendev.multibot.roles.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.RoleDAO;
import fr.gravendev.multibot.database.data.RoleData;
import net.dv8tion.jda.core.entities.Message;

import java.sql.SQLException;

public class MessageCommand implements CommandExecutor {
    
    private final DatabaseConnection databaseConnection;

    MessageCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getCommand() {
        return "message";
    }

    @Override
    public void execute(Message message, String[] args) {

        try {
            RoleDAO roleDAO = new RoleDAO(this.databaseConnection.getConnection());
            roleDAO.delete(roleDAO.get("message"));
            roleDAO.save(new RoleData("message", String.join(" ", args)));
            message.getChannel().sendMessage("le message a bien été changé en :\n" + String.join(" ", args)).queue();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
