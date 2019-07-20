package fr.gravendev.multibot.quiz;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

class CandidatureSender {

    static void send(DatabaseConnection databaseConnection, User user, Quiz quiz) {

        getCandidsChannel(databaseConnection, user).sendMessage(buildMessage(user, quiz)).queue(message -> {
            message.addReaction("\u2705").queue();
            message.addReaction("\u274C").queue();
        });

    }

    private static Message buildMessage(User user, Quiz quiz) {

        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.setContent(user.getAsMention());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.BLUE)
                .setAuthor(user.getAsTag(), user.getAvatarUrl(), user.getAvatarUrl());

        while (quiz.hasNextAnswer()) {
            embedBuilder.addField(quiz.getCurrentAnswer());
        }

        messageBuilder.setEmbed(embedBuilder.build());

        return messageBuilder.build();

    }

    private static TextChannel getCandidsChannel(DatabaseConnection databaseConnection, User user) {
        GuildIdDAO guildIdDAO = new GuildIdDAO(databaseConnection);
        long guildId = guildIdDAO.get("guild").id;
        long candidsChannelId = guildIdDAO.get("candids").id;

        return user.getJDA().getGuildById(guildId).getTextChannelById(candidsChannelId);
    }

}
