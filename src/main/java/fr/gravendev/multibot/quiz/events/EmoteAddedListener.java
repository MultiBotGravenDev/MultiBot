package fr.gravendev.multibot.quiz.events;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.quiz.QuizManager;
import fr.gravendev.multibot.quiz.events.emoteaddedexecutors.CandidsExecutor;
import fr.gravendev.multibot.quiz.events.emoteaddedexecutors.EmoteAddedExecutor;
import fr.gravendev.multibot.quiz.events.emoteaddedexecutors.ReadThisSaloonExecutor;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.Arrays;
import java.util.List;

public class EmoteAddedListener implements Listener<MessageReactionAddEvent> {

    private final List<EmoteAddedExecutor> executors;

    public EmoteAddedListener(QuizManager quizManager, DatabaseConnection databaseConnection) {
        this.executors = Arrays.asList(
                new ReadThisSaloonExecutor(quizManager, databaseConnection),
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
                .filter(executor -> executor.getSaloonId() == event.getChannel().getIdLong())
                .findAny()
                .ifPresent(executor -> executor.execute(event));

    }

}
