package fr.gravendev.multibot.commands.commands;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.CustomCommandDAO;
import fr.gravendev.multibot.database.data.CustomCommandData;
import net.dv8tion.jda.core.entities.Message;

import java.sql.SQLException;

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

        try {
            new CustomCommandDAO(this.databaseConnection).delete(new CustomCommandData(args[0], ""));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
