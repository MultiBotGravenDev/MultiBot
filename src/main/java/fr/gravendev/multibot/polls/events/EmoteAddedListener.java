package fr.gravendev.multibot.polls.events;

import fr.gravendev.multibot.events.Listener;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

public class EmoteAddedListener implements Listener<MessageReactionAddEvent> {

    @Override
    public Class<MessageReactionAddEvent> getEventClass() {
        return MessageReactionAddEvent.class;
    }

    @Override
    public void executeListener(MessageReactionAddEvent event) {

    }
}
