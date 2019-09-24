package fr.gravendev.multibot.quiz.events;

import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.quiz.QuizManager;
import fr.gravendev.multibot.quiz.events.emoteaddedexecutors.CandidsExecutor;
import fr.gravendev.multibot.quiz.events.emoteaddedexecutors.EmoteAddedExecutor;
import fr.gravendev.multibot.quiz.events.emoteaddedexecutors.ReadThisChannelExecutor;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.Arrays;
import java.util.List;

public class EmoteAddedListener implements Listener<MessageReactionAddEvent> {

    private final List<EmoteAddedExecutor> executors;

    public EmoteAddedListener(QuizManager quizManager) {
        this.executors = Arrays.asList(
                new ReadThisChannelExecutor(quizManager),
                new CandidsExecutor()
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
                .filter(executor -> executor.getChannelId().equals(event.getChannel().getId()))
                .findAny()
                .ifPresent(executor -> executor.execute(event));

    }

}
