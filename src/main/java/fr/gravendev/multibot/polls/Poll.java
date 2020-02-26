package fr.gravendev.multibot.polls;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;

import java.awt.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

class Poll {
    private static final String[] EMOTES = {
            "1\ufe0f\u20e3", "2\ufe0f\u20e3", "3\ufe0f\u20e3",
            "4\ufe0f\u20e3", "5\ufe0f\u20e3", "6\ufe0f\u20e3",
            "7\ufe0f\u20e3", "8\ufe0f\u20e3", "9\ufe0f\u20e3"
    };

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
        if (title.isEmpty()) this.title = " ";
        update();
    }

    void setChoice(int choiceNumber, String choice) {
        if (!choice.isEmpty()) {
            this.choices.put(choiceNumber, choice);
            String emote = EMOTES.length >= choiceNumber && choiceNumber > 0 ? EMOTES[choiceNumber - 1] : " ";
            this.emotes.put(choiceNumber, emote);
        } else {
            this.choices.remove(choiceNumber);
            this.emotes.remove(choiceNumber);
        }
        update();
    }
    
    boolean hasEmote(String emote) {
        return this.emotes.containsValue(emote);
    }

    void setEmote(int numberEmote, String emote) {
        if (this.choices.containsKey(numberEmote)) {
            // Find any choice with the same emote and
            // swap its emote with this choice
            this.emotes.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(emote))
                    .findAny()
                    .ifPresent(entry -> {
                        String newEmote = this.emotes.get(numberEmote);
                        entry.setValue(newEmote);
                    });
            
            this.emotes.put(numberEmote, emote);
        }

        update();
    }

    private void removeWrongReactions(List<MessageReaction> reactions) {
        boolean removeNexts = false;
        
        for (int i = 0; i < reactions.size(); i++) {
            MessageReaction reaction = reactions.get(i);

            if (!reaction.isSelf()) {
                continue;
            }

            String emote = reaction.getReactionEmote().getName();
            
            if (removeNexts || !emote.equals(this.emotes.get(i + 1))) {
                reaction.removeReaction().queue();
                removeNexts = true;
            }
        }
    }
    
    private void update() {
        this.user.openPrivateChannel().queue(privateChannel ->
                privateChannel.retrieveMessageById(this.messageId).queue(message -> {
                    List<MessageReaction> reactions = message.getReactions();
                    removeWrongReactions(reactions);
                    
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
            return;
        }
        buildMessage().setContent(this.user.getAsMention()).sendTo(channel).queue(message -> {
            message.addReaction("\u2705").queue();
            message.addReaction("\u274C").queue();
        });
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
        if (title == null) {
            title = " ";
        }
        return title.equalsIgnoreCase(this.title);
    }
}
