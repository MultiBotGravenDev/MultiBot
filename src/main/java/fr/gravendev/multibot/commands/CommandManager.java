package fr.gravendev.multibot.commands;

import fr.gravendev.multibot.commands.commands.AboutCommandExecutor;
import net.dv8tion.jda.core.entities.Message;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CommandManager {

    private final List<CommandExecutor> commandExecutors;

    public CommandManager() {
        commandExecutors = Arrays.asList(
                new AboutCommandExecutor()
        );
    }

    public boolean executeCommand(Message message) {

        String contentDisplay = message.getContentDisplay();

        String firstWord = contentDisplay.contains(" ")
                ? contentDisplay.substring(1, contentDisplay.indexOf(" "))
                : contentDisplay.substring(1);

        Optional<CommandExecutor> optionalCommandExecutor = this.commandExecutors.stream()
                .filter(commandExecutor -> commandExecutor.getCommand().equalsIgnoreCase(firstWord))
                .findAny();

        if (optionalCommandExecutor.isPresent()) {
            optionalCommandExecutor.get().execute(message);
            return true;
        } else {
            return false;
        }


    }

}
