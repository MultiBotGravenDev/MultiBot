package fr.gravendev.multibot.events.listeners;

import fr.gravendev.multibot.events.Listener;
import net.dv8tion.jda.core.events.emote.EmoteAddedEvent;

public class EmoteAddedListener implements Listener<EmoteAddedEvent> {

    @Override
    public Class<EmoteAddedEvent> getEventClass() {
        return EmoteAddedEvent.class;
    }

    @Override
    public void executeListener(EmoteAddedEvent event) {

    }

}
