package fr.gravendev.multibot.commands.commands.customs;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.CustomCommandDAO;
import fr.gravendev.multibot.database.data.CustomCommandData;
import net.dv8tion.jda.core.entities.Message;

import java.sql.SQLException;
import java.util.Arrays;

public class SetCommand implements CommandExecutor {

    private final DatabaseConnection databaseConnection;

    public SetCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getCommand() {
        return "set";
    }

    @Override
    public void execute(Message message, String[] args) {

        try {
            CustomCommandData customCommandData = new CustomCommandData(args[0], String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
            new CustomCommandDAO(this.databaseConnection).save(customCommandData);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
