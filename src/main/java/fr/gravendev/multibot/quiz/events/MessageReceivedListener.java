package fr.gravendev.multibot.quiz.events;

import fr.gravendev.multibot.commands.CommandManager;

import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.quiz.QuizManager;
import net.dv8tion.jda.core.entities.ChannelType;
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

        if (event.getAuthor().isBot()) return;

        if (event.getChannel().getType() == ChannelType.PRIVATE && this.quizManager.isWaitingFor(event.getAuthor())) {
            this.quizManager.registerResponse(event.getAuthor(), event.getMessage().getContentDisplay());
            this.quizManager.send(event.getAuthor());
        }

    }

}
