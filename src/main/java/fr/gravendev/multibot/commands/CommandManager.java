package fr.gravendev.multibot.commands;

import fr.gravendev.multibot.commands.commands.AboutCommandExecutor;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CommandManager {

    private final List<CommandExecutor> commandExecutors;
    private final char prefix;

    public CommandManager(char prefix) {
        this.prefix = prefix;
        commandExecutors = Arrays.asList(
                new AboutCommandExecutor()
        );
    }

    public boolean executeCommand(Message message) {

        User user = message.getAuthor();

        String content = message.getContentRaw();
        if(user.isBot() || content.length() <= 1) return false;

        char prefix = content.charAt(0);
        if(prefix != this.prefix) return false;

        String command = content.substring(1);
        String[] args = command.split(" +");

        Optional<CommandExecutor> optionalCommandExecutor = this.commandExecutors.stream()
                .filter(commandExecutor -> commandExecutor.getCommand().equalsIgnoreCase(args[0]))
                .findAny();

        if (optionalCommandExecutor.isPresent()) {

            CommandExecutor commandExecutor = optionalCommandExecutor.get();
            ChannelType commandChannelType = commandExecutor.getChannelType();

            if(commandChannelType == ChannelType.ALL || commandChannelType.isEqualsTo(message.getChannelType())) {
                commandExecutor.execute(message, Arrays.copyOfRange(args, 1, args.length));
                return true;
            }

        }

        return false;
    }

}
