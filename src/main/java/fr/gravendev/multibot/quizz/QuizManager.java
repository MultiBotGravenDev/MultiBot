package fr.gravendev.multibot.quizz;

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
        this.quizs.put(user.getIdLong(), new Quiz(this.databaseConnection));

        try {
            String startMessage = new QuizMessageDAO(this.databaseConnection.getConnection()).get(1 + "").message;
            user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(startMessage).queue());
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
