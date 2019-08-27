package fr.gravendev.multibot.quiz.events;


import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.quiz.QuizManager;
import fr.gravendev.multibot.quiz.WelcomeMessagesSetManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MessageReceivedListener implements Listener<MessageReceivedEvent> {

    private final QuizManager quizManager;
    private final WelcomeMessagesSetManager welcomeMessagesSetManager;

    public MessageReceivedListener(QuizManager quizManager, WelcomeMessagesSetManager welcomeMessagesSetManager) {
        this.quizManager = quizManager;
        this.welcomeMessagesSetManager = welcomeMessagesSetManager;
    }

    @Override
    public Class<MessageReceivedEvent> getEventClass() {
        return MessageReceivedEvent.class;
    }

    @Override
    public void executeListener(MessageReceivedEvent event) {

        Message message = event.getMessage();
        User author = message.getAuthor();

        if (author.isBot()) return;

        if (this.quizManager.isWaitingFor(author)) {

            this.quizManager.registerResponse(author, message.getContentDisplay());
            this.quizManager.send(author);

        } else if (this.welcomeMessagesSetManager.isWaitingFor(message.getAuthor())){

            this.welcomeMessagesSetManager.registerMessage(message);

        }

    }

}
