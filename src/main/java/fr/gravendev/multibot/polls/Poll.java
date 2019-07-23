package fr.gravendev.multibot.polls;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

public class Poll {

    private final User user;
    private final long messageId;
    private Color color;
    private String title = " ";

    Poll(User user, long messageId) {
        this.user = user;
        this.messageId = messageId;
    }

    public void setColor(Colors color) {
        this.color = color.color;
        update();
    }

    public void setTitle(String title) {
        this.title = title;
        update();
    }

    private void update() {
        this.user.openPrivateChannel().queue(privateChannel ->
                privateChannel.getMessageById(this.messageId).queue(message ->
                        message.editMessage(new MessageBuilder(message)
                                .setEmbed(new EmbedBuilder()
                                        .setColor(this.color)
                                        .setTitle(this.title)
                                        .setFooter(this.user.getName(), this.user.getAvatarUrl())
                                        .build())
                                .build()).queue()));
    }

}
