package fr.gravendev.multibot.quiz;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.QuizMessageDAO;
import net.dv8tion.jda.core.entities.User;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class QuizManager {

    private final DatabaseConnection databaseConnection;
    private Map<Long, Quiz> quizs = new HashMap<>();

    public QuizManager(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public void createQuiz(User user) {
        this.quizs.put(user.getIdLong(), new Quiz(this.databaseConnection, user));

        try {
            String startMessage = new QuizMessageDAO(this.databaseConnection.getConnection()).get("start").message;
            user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(startMessage).queue());
            send(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void send(User user) {
        if (!this.quizs.get(user.getIdLong()).send()) {
            this.quizs.remove(user.getIdLong());
        }
    }

    public void removeQuiz(User user) {
        this.quizs.remove(user.getIdLong());
    }

    public boolean isWaitingFor(User user) {
        return this.quizs.containsKey(user.getIdLong());
    }

    public void registerResponse(User user, String contentDisplay) {
        this.quizs.get(user.getIdLong()).registerResponse(contentDisplay);
    }
}
