package fr.gravendev.multibot.commands;

import net.dv8tion.jda.core.entities.Message;

import java.util.Arrays;
import java.util.List;

public class CommandManager {

    private final List<Command> commands;

    public CommandManager() {
        commands = Arrays.asList(

        );
    }

    public boolean executeCommand(Message message) {

        String contentDisplay = message.getContentDisplay();

        String firstWord = contentDisplay.contains(" ")
                ? contentDisplay.substring(1, contentDisplay.indexOf(" "))
                : contentDisplay.substring(1);

        return this.commands.stream()
                .filter(command -> command.getCommand().equalsIgnoreCase(firstWord))
                .findAny()
                .map(command -> command.execute(message))
                .orElse(false);

    }

}
