package fr.gravendev.multibot.quiz.events;


import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.quiz.MemberQuestionsManager;
import fr.gravendev.multibot.quiz.QuizManager;
import fr.gravendev.multibot.quiz.WelcomeMessagesSetManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MessageReceivedListener implements Listener<MessageReceivedEvent> {

    private final QuizManager quizManager;
    private final WelcomeMessagesSetManager welcomeMessagesSetManager;
    private final MemberQuestionsManager questionsManager;

    public MessageReceivedListener(QuizManager quizManager, WelcomeMessagesSetManager welcomeMessagesSetManager, MemberQuestionsManager questionsManager) {
        this.quizManager = quizManager;
        this.welcomeMessagesSetManager = welcomeMessagesSetManager;
        this.questionsManager = questionsManager;
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

        doQuizActions(message, author);
        doWelcomeMessageActions(message);

    }

    private void doWelcomeMessageActions(Message message) {
        if (this.welcomeMessagesSetManager.isWaitingFor(message.getAuthor())) {

            if (message.getContentDisplay().startsWith("&quiz") && welcomeMessagesSetManager.hasLessOneSentence(message.getMember())) {
                return;
            }
            this.welcomeMessagesSetManager.registerMessage(message);

        }
    }

    private void doQuizActions(Message message, User author) {

//        if (this.questionsManager.isWaitingFor(author)) {

        if (this.quizManager.isWaitingFor(author)) {

//            this.questionsManager.registerResponse(author, message.getContentDisplay());

            this.quizManager.registerResponse(author, message.getContentDisplay());
            this.quizManager.send(author);

        }

    }

}
