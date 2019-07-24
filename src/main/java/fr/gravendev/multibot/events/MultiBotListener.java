package fr.gravendev.multibot.events;

import fr.gravendev.multibot.commands.CommandManager;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.polls.PollsManager;
import fr.gravendev.multibot.quiz.WelcomeMessagesSetManager;
import fr.gravendev.multibot.quiz.QuizManager;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;

import java.util.Arrays;
import java.util.List;

public class MultiBotListener implements EventListener {

    private final List<Listener> events;

    public MultiBotListener(CommandManager commandManager, DatabaseConnection databaseConnection, QuizManager quizManager, WelcomeMessagesSetManager welcomeMessagesSetManager, PollsManager pollsManager) {
        events = Arrays.asList(
                new fr.gravendev.multibot.commands.MessageReceivedListener(commandManager),
                new fr.gravendev.multibot.rank.MessageReceivedListener(databaseConnection),
                new fr.gravendev.multibot.logs.MessageReceivedListener(databaseConnection),
                new fr.gravendev.multibot.quiz.events.MessageReceivedListener(quizManager, welcomeMessagesSetManager),
                new fr.gravendev.multibot.quiz.events.EmoteAddedListener(quizManager, databaseConnection),
                new fr.gravendev.multibot.quiz.events.EmoteRemovedListener(quizManager),
                new fr.gravendev.multibot.roles.listeners.ReactionAddedListener(databaseConnection),
                new fr.gravendev.multibot.roles.listeners.ReactionRemovedListener(databaseConnection),
                new fr.gravendev.multibot.votes.events.EmoteAddedListener(databaseConnection),
                new fr.gravendev.multibot.votes.events.RoleAddedEvent(databaseConnection),
                new fr.gravendev.multibot.polls.events.EmoteAddedListener(databaseConnection, pollsManager),
                new ReadyListener(databaseConnection)
        );
    }

    @Override
    public void onEvent(Event event) {

        events.stream()
                .filter(listener -> listener.getEventClass().equals(event.getClass()))
                .forEach(listener -> listener.executeListener(event));

    }

}
