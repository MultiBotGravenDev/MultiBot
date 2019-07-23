package fr.gravendev.multibot.polls;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Poll {

    private static final String EMOTES = ":one: :two: :three: :for: :five: :six: :seven: :eight: :nine: ";

    private final User user;
    private final long messageId;
    private Color color;
    private String title = " ";

    private Map<Integer, String> choices = new TreeMap<>();
    private Map<Integer, String> emotes = new HashMap<>();

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

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(this.color)
                .setTitle(this.title)
                .setFooter(this.user.getName(), this.user.getAvatarUrl());

        this.choices.forEach((numberChoice, emote) -> embedBuilder
                .getDescriptionBuilder()
                .append(" ")
                .append(this.emotes.get(numberChoice))
                .append(emote)
                .append("\n\n"));

        this.user.openPrivateChannel().queue(privateChannel ->
                privateChannel.getMessageById(this.messageId).queue(message ->
                        message.editMessage(new MessageBuilder(message)
                                .setEmbed(embedBuilder.build())
                                .build()).queue()));
    }

    void setChoice(int choiceNumber, String choice) {
        if (this.choices.size() >= 36) return;
        if (!choice.isEmpty()) {
            this.choices.put(choiceNumber, choice);
            this.emotes.put(choiceNumber, EMOTES.split(" ")[choiceNumber - 1]);
        } else {
            this.choices.remove(choiceNumber);
            this.emotes.remove(choiceNumber);
        }
        update();
    }

    void setEmote(int numberEmote, String emote) {
        this.emotes.put(numberEmote, emote);
        update();
    }

}
