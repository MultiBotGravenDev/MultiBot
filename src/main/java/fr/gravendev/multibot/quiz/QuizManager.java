package fr.gravendev.multibot.quiz;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.QuizMessageDAO;
import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;
import java.util.Map;

public class QuizManager {

    private final DatabaseConnection databaseConnection;
    private final QuizMessageDAO quizMessageDAO;
    private Map<Long, Quiz> quizs = new HashMap<>();

    public QuizManager(DatabaseConnection databaseConnection) {
        quizMessageDAO = new QuizMessageDAO(databaseConnection);
        this.databaseConnection = databaseConnection;
    }

    public void createQuiz(User user) {
        this.quizs.put(user.getIdLong(), new Quiz(this.quizMessageDAO));
        send(user);
        send(user);
    }

    public void send(User user) {
        long userId = user.getIdLong();
        Quiz quiz = this.quizs.get(userId);
        quiz.send(user);
        if (!quiz.hashNextQuestion()) {
            CandidatureSender.send(this.databaseConnection, user, quiz);
            this.quizs.remove(userId);
        }
    }

    public void removeQuiz(User user) {
        this.quizs.remove(user.getIdLong());
    }

    public boolean isWaitingFor(User user) {
        return this.quizs.containsKey(user.getIdLong());
    }

    public void registerResponse(User user, String response) {
        this.quizs.get(user.getIdLong()).registerResponse(response);
    }

}
