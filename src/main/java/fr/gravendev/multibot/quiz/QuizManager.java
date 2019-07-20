package fr.gravendev.multibot.quiz;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.database.dao.QuizMessageDAO;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
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

        String startMessage = new QuizMessageDAO(this.databaseConnection).get("start").message;
        user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(startMessage).queue());
        send(user);

    }

    public void send(User user) {
        long userId = user.getIdLong();
        if (!this.quizs.get(userId).send()) {
            sendResponses(user, this.quizs.get(userId));
            this.quizs.remove(userId);
        }
    }

    private void sendResponses(User user, Quiz quiz) {

        GuildIdDAO guildIdDAO = new GuildIdDAO(this.databaseConnection);
        long guildId = guildIdDAO.get("guild").id;
        long candidsChannelId = guildIdDAO.get("candids").id;

        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.setContent(user.getAsMention());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.BLUE)
                .setAuthor(user.getAsTag(), user.getAvatarUrl(), user.getAvatarUrl());

        while (quiz.nextAnswer()) {
            embedBuilder.addField(quiz.getCurrentAnswer());
        }

        messageBuilder.setEmbed(embedBuilder.build())
                .sendTo(user.getJDA().getGuildById(guildId).getTextChannelById(candidsChannelId)).queue(message -> {
            message.addReaction("\u2705").queue();
            message.addReaction("\u274C").queue();
        });

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
