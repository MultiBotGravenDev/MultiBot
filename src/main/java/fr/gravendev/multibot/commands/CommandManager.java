package fr.gravendev.multibot.commands;

import fr.gravendev.multibot.commands.commands.AboutCommand;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.commands.commands.UserinfoCommand;
import fr.gravendev.multibot.rank.RankCommand;
import fr.gravendev.multibot.quiz.commands.QuizCommand;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.roles.commands.RoleCommand;
import net.dv8tion.jda.core.entities.Message;

import java.util.Arrays;
import java.util.List;

public class CommandManager {

    private final List<CommandExecutor> commandExecutors;
    private final char prefix;

    public CommandManager(char prefix, DatabaseConnection databaseConnection) {
        this.prefix = prefix;
        commandExecutors = Arrays.asList(
                new AboutCommand(),
                new QuizCommand(databaseConnection),
                new RankCommand(databaseConnection),
                new RoleCommand(databaseConnection),
                new UserinfoCommand()
        );
    }

    void executeCommand(Message message) {

        String content = message.getContentRaw();

        if (content.length() == 0 || content.charAt(0) != this.prefix) return;

        String[] args = content.substring(1).split(" +");

        this.commandExecutors.stream()
                .filter(commandExecutor -> commandExecutor.getCommand().equalsIgnoreCase(args[0]))
                .filter(commandExecutor -> commandExecutor.canExecute(message))
                .findAny()
                .ifPresent(commandExecutor -> commandExecutor.execute(message, Arrays.copyOfRange(args, 1, args.length)));

    }

}
