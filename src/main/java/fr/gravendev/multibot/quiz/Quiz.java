package fr.gravendev.multibot.quiz;

import fr.gravendev.multibot.database.data.MessageData;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.QuizMessageDAO;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

class Quiz {

    private final DatabaseConnection databaseConnection;
    private final User user;
    private int questionIndex;
    private int answerIndex;
    private final Map<Integer, String> responses = new HashMap<>();

    Quiz(DatabaseConnection databaseConnection, User user) {
        this.databaseConnection = databaseConnection;
        this.user = user;
        this.questionIndex = 1;
        this.answerIndex = 0;
    }

    boolean send() {

        try {
            QuizMessageDAO quizMessageDAO = new QuizMessageDAO(this.databaseConnection);
            MessageData messageData = quizMessageDAO.get(this.questionIndex + "");

            if (messageData != null) {

                this.user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(messageData.message).queue());
                return true;

            } else {

                this.user.openPrivateChannel().queue(privateChannel -> {
                    try {
                        privateChannel.sendMessage(quizMessageDAO.get("stop").message).queue();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                return false;

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;

    }

    void registerResponse(String contentDisplay) {
        this.responses.put(this.questionIndex++, contentDisplay);
    }

    boolean nextAnswer() {
        ++this.answerIndex;
        return this.responses.get(this.answerIndex) != null;
    }

    MessageEmbed.Field getCurrentAnswer() {
        try {
            return new MessageEmbed.Field(
                    new QuizMessageDAO(this.databaseConnection).get(this.answerIndex + "").message,
                    this.responses.get(this.answerIndex),
                    false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new MessageEmbed.Field("", "", false);
    }
}
