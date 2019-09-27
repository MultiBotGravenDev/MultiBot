package fr.gravendev.multibot.quiz;

import fr.gravendev.multibot.utils.Configuration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

class CandidatureSender {

    static void send(User user, Quiz quiz) {

        getCandidsChannel(user).sendMessage(buildMessage(user, quiz)).queue(message -> {
            message.addReaction("\u2705").queue();
            message.addReaction("\u274C").queue();
        });

    }

    private static Message buildMessage(User user, Quiz quiz) {

        MessageBuilder builder = new MessageBuilder();
        builder.setContent(user.getAsMention());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.decode("#1A8CFE"))
                .setAuthor(user.getAsTag(), user.getAvatarUrl(), user.getAvatarUrl());

        embedBuilder.setTitle(user.getName());

        while (quiz.hasNextAnswer()) {
            embedBuilder.addField(quiz.getCurrentAnswer());
        }

        return builder.setEmbed(embedBuilder.build()).build();

    }

    private static TextChannel getCandidsChannel(User user) {
        String candidsChannelId = Configuration.CANDIDS.getValue();

        Guild guild = user.getJDA().getGuildById(Configuration.GUILD.getValue());
        if (guild == null) {
            return null;
        }
        return guild.getTextChannelById(candidsChannelId);
    }

}
