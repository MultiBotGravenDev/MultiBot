package fr.gravendev.multibot.commands;

import fr.gravendev.multibot.commands.commands.AboutCommand;
import fr.gravendev.multibot.commands.commands.RankCommand;
import fr.gravendev.multibot.commands.commands.RolesCommand;
import fr.gravendev.multibot.commands.commands.WelcomeMessageCommand;
import fr.gravendev.multibot.database.DatabaseConnection;
import net.dv8tion.jda.core.entities.Message;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CommandManager {

    private final List<CommandExecutor> commandExecutors;
    private final char prefix;

    public CommandManager(char prefix, DatabaseConnection databaseConnection) {
        this.prefix = prefix;
        commandExecutors = Arrays.asList(
                new AboutCommand(),
                new WelcomeMessageCommand(databaseConnection),
                new RankCommand(databaseConnection),
                new RolesCommand(databaseConnection)
        );
    }

    public boolean executeCommand(Message message) {

        String content = message.getContentRaw();

        char prefix = content.charAt(0);
        if (prefix != this.prefix) return false;

        String command = content.substring(1);
        String[] args = command.split(" +");

        Optional<CommandExecutor> optionalCommandExecutor = this.commandExecutors.stream()
                .filter(commandExecutor -> commandExecutor.getCommand().equalsIgnoreCase(args[0]))
                .findAny();

        if (optionalCommandExecutor.isPresent()) {

            CommandExecutor commandExecutor = optionalCommandExecutor.get();

            if (commandExecutor.isAuthorizedChannel(message.getChannel()) && commandExecutor.isAuthorizedMember(message.getMember())) {

                commandExecutor.execute(message, Arrays.copyOfRange(args, 1, args.length));
                return true;

            }

        }

        return false;
    }

}
