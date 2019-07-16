package fr.gravendev.multibot.events.listeners;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.events.listeners.emoteaddedexecutors.CandidsExecutor;
import fr.gravendev.multibot.events.listeners.emoteaddedexecutors.ReadThisSaloonExecutor;
import fr.gravendev.multibot.quiz.QuizManager;
import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.util.Arrays;
import java.util.List;

public class EmoteAddedListener implements Listener<MessageReactionAddEvent> {

    private final List<EmoteAddedExecutor> executors;

    public EmoteAddedListener(QuizManager quizManager, DatabaseConnection databaseConnection) {
        this.executors = Arrays.asList(
                new ReadThisSaloonExecutor(quizManager),
                new CandidsExecutor(databaseConnection)
        );
    }

    @Override
    public Class<MessageReactionAddEvent> getEventClass() {
        return MessageReactionAddEvent.class;
    }

    @Override
    public void executeListener(MessageReactionAddEvent event) {

        if (event.getUser().isBot()) return;

        this.executors.stream()
                .filter(executor -> executor.getSaloon().equalsIgnoreCase(event.getChannel().getName()))
                .findAny()
                .ifPresent(executor -> executor.execute(event));

    }

}
