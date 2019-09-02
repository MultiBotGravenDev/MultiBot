package fr.gravendev.multibot.events;

import fr.gravendev.multibot.commands.CommandManager;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.polls.PollsManager;
import fr.gravendev.multibot.quiz.QuizManager;
import fr.gravendev.multibot.quiz.WelcomeMessagesSetManager;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class MultiBotListener implements EventListener {

    private final List<Listener> events;

    public MultiBotListener(CommandManager commandManager, DatabaseConnection databaseConnection, QuizManager quizManager, WelcomeMessagesSetManager welcomeMessagesSetManager, PollsManager pollsManager) {
        events = Arrays.asList(
                new fr.gravendev.multibot.commands.MessageReceivedListener(commandManager),
                new fr.gravendev.multibot.rank.MessageReceivedListener(databaseConnection),
                new fr.gravendev.multibot.logs.MessageReceivedListener(databaseConnection),
                new fr.gravendev.multibot.logs.MessageDeleteListener(databaseConnection),
                new fr.gravendev.multibot.quiz.events.MessageReceivedListener(quizManager, welcomeMessagesSetManager),
                new fr.gravendev.multibot.quiz.events.EmoteAddedListener(quizManager, databaseConnection),
                new fr.gravendev.multibot.quiz.events.EmoteRemovedListener(quizManager),
                new fr.gravendev.multibot.roles.listeners.ReactionAddedListener(databaseConnection),
                new fr.gravendev.multibot.roles.listeners.ReactionRemovedListener(databaseConnection),
                new fr.gravendev.multibot.votes.events.EmoteAddedListener(databaseConnection),
                new fr.gravendev.multibot.votes.events.RoleAddedEvent(databaseConnection),
                new fr.gravendev.multibot.polls.events.EmoteAddedListener(databaseConnection, pollsManager),
                new fr.gravendev.multibot.moderation.events.MessageReceivedListener(databaseConnection),
                new ReadyListener(databaseConnection),
                new MemberJoinListener(databaseConnection)
        );
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {

        events.stream()
                .filter(listener -> listener.getEventClass().equals(event.getClass()))
                .forEach(listener -> listener.executeListener(event));

    }

}
