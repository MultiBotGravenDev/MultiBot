package fr.gravendev.multibot.quiz.events;

import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.quiz.QuizManager;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MessageReceivedListener implements Listener<MessageReceivedEvent> {

    private final QuizManager quizManager;

    public MessageReceivedListener(QuizManager quizManager) {
        this.quizManager = quizManager;
    }

    @Override
    public Class<MessageReceivedEvent> getEventClass() {
        return MessageReceivedEvent.class;
    }

    @Override
    public void executeListener(MessageReceivedEvent event) {

        User author = event.getAuthor();
        if (author.isBot() || !this.quizManager.isWaitingFor(author)) return;

        this.quizManager.registerResponse(author, event.getMessage().getContentDisplay());
        this.quizManager.send(author);

    }


}
