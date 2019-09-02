package fr.gravendev.multibot.commands;

import fr.gravendev.multibot.events.Listener;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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
        User messageAuthor = event.getAuthor();
        if (messageAuthor.isBot()) {
            return;
        }

        Message message = event.getMessage();
        commandManager.executeCommand(message);
    }
}
