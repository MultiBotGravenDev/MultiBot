package fr.gravendev.multibot.polls;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.requests.RestAction;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

class Poll {

    private static final String[] EMOTES = {"1\u20E3", "2\u20E3", "3\u20E3", "4\u20E3", "5\u20E3", "6\u20E3", "7\u20E3", "8\u20E3", "9\u20E3"};

    private final User user;
    private final long messageId;
    private final Map<Integer, String> choices = new TreeMap<>();
    private final Map<Integer, String> emotes = new HashMap<>();
    private Color color;
    private String title = " ";

    Poll(User user, long messageId) {
        this.user = user;
        this.messageId = messageId;
    }

    void setColor(Colors color) {
        this.color = color.color;
        update();
    }

    void setTitle(String title) {
        this.title = title;
        update();
    }

    void setChoice(int choiceNumber, String choice) {
        if (!choice.isEmpty()) {
            this.choices.put(choiceNumber, choice);
            String emote = EMOTES.length >= choiceNumber ? EMOTES[choiceNumber - 1] : " ";
            this.emotes.put(choiceNumber, emote);
        } else {
            this.choices.remove(choiceNumber);
            this.emotes.remove(choiceNumber);
        }
        update();
    }

    void setEmote(int numberEmote, String emote) {
        if (this.choices.containsKey(numberEmote)) {
            this.emotes.put(numberEmote, emote);
        }
        update();
    }

    private void update() {
        this.user.openPrivateChannel().queue(privateChannel ->
                privateChannel.getMessageById(this.messageId).queue(message -> {
                    User selfUser = message.getJDA().getSelfUser();
                    message.getReactions().forEach(messageReaction -> {
                        if (!this.emotes.values().contains(messageReaction.getReactionEmote().getName())) {
                            messageReaction.removeReaction(selfUser).queue();
                        }
                    });
                    message.editMessage(buildMessage().build()).queue(message1 -> this.emotes.values()
                            .stream()
                            .filter(emote -> !emote.equalsIgnoreCase(" "))
                            .map(message1::addReaction)
                            .forEach(RestAction::queue));
                }));
    }

    void finish(TextChannel channel, boolean isValidated) {

        if (isValidated) {
            buildMessage().sendTo(channel).queue(message ->
                    this.emotes.values()
                            .stream()
                            .map(message::addReaction)
                            .forEach(RestAction::queue)
            );
        } else {
            buildMessage().setContent(this.user.getAsMention()).sendTo(channel).queue(message -> {
                message.addReaction("\u2705").queue();
                message.addReaction("\u274C").queue();
            });
        }

    }

    private MessageBuilder buildMessage() {

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(this.color)
                .setTitle(this.title)
                .setFooter(this.user.getName(), this.user.getAvatarUrl());

        this.choices.forEach((numberChoice, choice) -> embedBuilder
                .getDescriptionBuilder()
                .append(this.emotes.get(numberChoice))
                .append(" ")
                .append(choice)
                .append("\n\n"));

        return new MessageBuilder().setEmbed(embedBuilder.build());

    }

    boolean isSameTitle(String title) {
        if (title == null) title = " ";
        return title.equalsIgnoreCase(this.title);
    }

}
