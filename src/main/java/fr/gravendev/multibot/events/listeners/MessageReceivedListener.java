package fr.gravendev.multibot.events.listeners;

import fr.gravendev.multibot.commands.CommandManager;
import fr.gravendev.multibot.events.Listener;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MessageReceivedListener implements Listener<MessageReceivedEvent> {

    private final CommandManager commandManager;

    public MessageReceivedListener(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public Class<MessageReceivedEvent> getEventClass() {
        return MessageReceivedEvent.class;
    }

    @Override
    public void executeListener(MessageReceivedEvent event) {

        if (commandManager.executeCommand(event.getMessage())) return;

    }

}
