package fr.gravendev.multibot.events;

import fr.gravendev.multibot.commands.CommandManager;
import fr.gravendev.multibot.events.listeners.EmoteAddedListener;
import fr.gravendev.multibot.events.listeners.EmoteRemovedListener;
import fr.gravendev.multibot.events.listeners.MessageReceivedListener;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;

import java.util.Arrays;
import java.util.List;

public class MultiBotListener implements EventListener {

    private final List<Listener> events;

    public MultiBotListener(CommandManager commandManager) {
        events = Arrays.asList(
                new MessageReceivedListener(commandManager),
                new EmoteAddedListener(),
                new EmoteRemovedListener()
        );
    }

    @Override
    public void onEvent(Event event) {

        events.stream()
                .filter(listener -> listener.getEventClass().equals(event.getClass()))
                .findAny()
                .ifPresent(listener -> listener.executeListener(event));

    }

}
