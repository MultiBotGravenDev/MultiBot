package fr.gravendev.multibot.events.listeners;

import fr.gravendev.multibot.events.Listener;
import net.dv8tion.jda.core.events.emote.EmoteRemovedEvent;

public class EmoteRemovedListener implements Listener<EmoteRemovedEvent> {

    @Override
    public Class<EmoteRemovedEvent> getEventClass() {
        return EmoteRemovedEvent.class;
    }

    @Override
    public void executeListener(EmoteRemovedEvent event) {

    }

}
