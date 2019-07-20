package fr.gravendev.multibot.quiz;

import fr.gravendev.multibot.database.dao.QuizMessageDAO;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;
import java.util.Map;

class Quiz {

    private final QuizMessageDAO quizMessageDAO;

    private int questionIndex;
    private int answerIndex;
    private final Map<Integer, String> responses = new HashMap<>();

    Quiz(QuizMessageDAO quizMessageDAO) {
        this.quizMessageDAO = quizMessageDAO;
        this.questionIndex = 0;
        this.answerIndex = 0;
    }

    void send(User user) {
        String message = this.quizMessageDAO.get(String.valueOf(this.questionIndex++)).message;
        user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(message).queue());
    }

    boolean hashNextQuestion() {
        return this.quizMessageDAO.get(String.valueOf(this.questionIndex)) != null;
    }

    void registerResponse(String contentDisplay) {
        this.responses.put(this.questionIndex - 1, contentDisplay);
    }

    boolean hasNextAnswer() {
        return this.responses.get(++this.answerIndex) != null;
    }

    MessageEmbed.Field getCurrentAnswer() {
        return new MessageEmbed.Field(
                this.quizMessageDAO.get(this.answerIndex + "").message,
                this.responses.get(this.answerIndex),
                false);
    }

}
