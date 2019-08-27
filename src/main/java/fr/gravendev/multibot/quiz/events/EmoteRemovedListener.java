package fr.gravendev.multibot.quiz.events;

import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.quiz.QuizManager;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

public class EmoteRemovedListener implements Listener<MessageReactionRemoveEvent> {

    private final QuizManager quizManager;

    public EmoteRemovedListener(QuizManager quizManager) {
        this.quizManager = quizManager;
    }

    @Override
    public Class<MessageReactionRemoveEvent> getEventClass() {
        return MessageReactionRemoveEvent.class;
    }

    @Override
    public void executeListener(MessageReactionRemoveEvent event) {

        if (event.getUser().isBot() || !event.getChannel().getName().equalsIgnoreCase("lisez-ce-salon")) return;
        this.quizManager.removeQuiz(event.getUser());

    }

}
