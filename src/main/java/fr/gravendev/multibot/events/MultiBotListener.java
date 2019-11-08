package fr.gravendev.multibot.events;

import fr.gravendev.multibot.commands.CommandManager;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.logs.GuildMessageDeleteListener;
import fr.gravendev.multibot.polls.PollsManager;
import fr.gravendev.multibot.quiz.MemberQuestionsManager;
import fr.gravendev.multibot.quiz.QuizManager;
import fr.gravendev.multibot.quiz.WelcomeMessagesSetManager;
import fr.gravendev.multibot.rank.GuildMessageReceivedListener;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class MultiBotListener implements EventListener {

    private final List<Listener> events;

    public MultiBotListener(CommandManager commandManager, DAOManager daoManager, QuizManager quizManager, WelcomeMessagesSetManager welcomeMessagesSetManager, PollsManager pollsManager, MemberQuestionsManager questionsManager) {

        events = Arrays.asList(
                new fr.gravendev.multibot.commands.MessageReceivedListener(commandManager),
                new GuildMessageReceivedListener(daoManager),
                new fr.gravendev.multibot.logs.GuildMessageReceivedListener(daoManager),
                new GuildMessageDeleteListener(daoManager),
                new fr.gravendev.multibot.quiz.events.MessageReceivedListener(quizManager, welcomeMessagesSetManager, questionsManager),
                new fr.gravendev.multibot.quiz.events.EmoteAddedListener(quizManager, questionsManager),
                new fr.gravendev.multibot.quiz.events.EmoteRemovedListener(quizManager),
                new fr.gravendev.multibot.roles.listeners.ReactionAddedListener(daoManager),
                new fr.gravendev.multibot.roles.listeners.ReactionRemovedListener(daoManager),
//                new fr.gravendev.multibot.votes.events.EmoteAddedListener(daoManager),
//                new fr.gravendev.multibot.votes.events.RoleAddedEvent(daoManager),
                new fr.gravendev.multibot.polls.events.EmoteAddedListener(pollsManager),
                new fr.gravendev.multibot.moderation.events.GuildMessageReceivedListener(daoManager),
                new ReadyListener(daoManager),
                new MemberJoinListener(daoManager)
        );
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {

        events.stream()
                .filter(listener -> listener.getEventClass().equals(event.getClass()))
                .forEach(listener -> listener.executeListener(event));

    }

}
