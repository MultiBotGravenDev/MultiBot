package fr.gravendev.multibot.events;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;

import java.util.Arrays;
import java.util.List;

public class MultiBotListener implements EventListener {

    private final List<Listener> EVENTS;

    public MultiBotListener() {
        EVENTS = Arrays.asList(

        );
    }

    @Override
    public void onEvent(Event event) {

        EVENTS.stream()
                .filter(listener -> listener.getEventClass().equals(event.getClass()))
                .findAny()
                .ifPresent(listener -> listener.executeListener(event));

    }

}
