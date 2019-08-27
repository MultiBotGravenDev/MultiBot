package fr.gravendev.multibot.events;

import net.dv8tion.jda.api.events.GenericEvent;

public interface Listener<T extends GenericEvent> {

    Class<T> getEventClass();

    void executeListener(T event);

}
