package fr.gravendev.multibot.events.listeners;

import fr.gravendev.multibot.events.Listener;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;

public class EmoteRemovedListener implements Listener<MessageReactionRemoveEvent> {

    @Override
    public Class<MessageReactionRemoveEvent> getEventClass() {
        return MessageReactionRemoveEvent.class;
    }

    @Override
    public void executeListener(MessageReactionRemoveEvent event) {

    }

}
