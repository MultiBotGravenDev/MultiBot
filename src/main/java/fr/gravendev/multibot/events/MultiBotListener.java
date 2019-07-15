package fr.gravendev.multibot.events;

import fr.gravendev.multibot.commands.CommandManager;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.events.listeners.EmoteAddedListener;
import fr.gravendev.multibot.events.listeners.EmoteRemovedListener;
import fr.gravendev.multibot.events.listeners.MessageReceivedListener;
import fr.gravendev.multibot.quizz.QuizManager;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;

import java.util.Arrays;
import java.util.List;

public class MultiBotListener implements EventListener {

    private final List<Listener> events;

    public MultiBotListener(CommandManager commandManager, DatabaseConnection databaseConnection, QuizManager quizManager) {
        events = Arrays.asList(
                new MessageReceivedListener(commandManager, databaseConnection, quizManager),
                new EmoteAddedListener(quizManager),
                new EmoteRemovedListener(quizManager)
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
