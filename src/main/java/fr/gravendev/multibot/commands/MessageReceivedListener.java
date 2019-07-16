package fr.gravendev.multibot.commands;

import fr.gravendev.multibot.events.Listener;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MessageReceivedListener implements Listener<MessageReceivedEvent> {

    @Override
    public Class<MessageReceivedEvent> getEventClass() {
        return MessageReceivedEvent.class;
    }

    @Override
    public void executeListener(MessageReceivedEvent event) {

    }

}
