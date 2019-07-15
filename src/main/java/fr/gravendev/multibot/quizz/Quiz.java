package fr.gravendev.multibot.quizz;

import fr.gravendev.multibot.data.MessageData;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.QuizMessageDAO;
import net.dv8tion.jda.core.entities.User;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

class Quiz {

    private final DatabaseConnection databaseConnection;
    private final User user;
    private int questionIndex;
    private final Map<Integer, String> responses = new HashMap<>();

    Quiz(DatabaseConnection databaseConnection, User user) {
        this.databaseConnection = databaseConnection;
        this.user = user;
        this.questionIndex = 3;
    }

    boolean send() {

        try {
            QuizMessageDAO quizMessageDAO = new QuizMessageDAO(this.databaseConnection.getConnection());
            MessageData messageData = quizMessageDAO.get(this.questionIndex++ + "");

            if (messageData != null) {

                this.user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(messageData.message).queue());
                return true;

            } else {

                this.user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(quizMessageDAO.get(2 + "").message).queue());
                return false;

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;

    }

    public void registerResponse(String contentDisplay) {
        this.responses.put(this.questionIndex, contentDisplay);
    }
}
