package fr.gravendev.multibot.quiz.events.emoteaddedexecutors;

import fr.gravendev.multibot.utils.Configuration;
import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;

public class CandidsExecutor implements EmoteAddedExecutor {

    @Override
    public String getChannelId() {
        return Configuration.CANDIDS.getValue();
    }

    @Override
    public void execute(MessageReactionAddEvent event) {

        String memberRoleId = Configuration.MEMBER.getValue();

        event.getChannel().retrieveMessageById(event.getMessageIdLong()).queue(message -> {

            MessageEmbed builder = editWithoutMember(event, message);

            if (message.getMentionedMembers().size() != 0) {
                Member member = message.getMentionedMembers().get(0);
                builder = editWithMember(event, memberRoleId, message, member);
            }

            if (builder != null) {
                message.clearReactions().queue();
                message.editMessage(new MessageBuilder(builder).build()).queue();
            }

        });

    }

    private MessageEmbed editWithoutMember(MessageReactionAddEvent event, Message message) {

        String validationMessage = " ";
        Color color = Color.decode("#FF0025");

        switch (event.getReactionEmote().getName()) {

            case "\u2705":
                validationMessage += "Acceptée par ";
                color = Color.decode("#008000");
                break;

            case "\u274C":
                validationMessage += "Refusée par ";
                break;

            default:
                event.getReaction().removeReaction(event.getUser()).queue();
                return null;

        }

        validationMessage += event.getUser().getAsTag();

        return new EmbedBuilder(message.getEmbeds().get(0))
                .setTitle(validationMessage)
                .setColor(color)
                .build();

    }

    private MessageEmbed editWithMember(MessageReactionAddEvent event, String memberRoleId, Message message, Member member) {
        String validationMessage = "";
        Color color = Color.decode("#FF0025");

        switch (event.getReactionEmote().getName()) {

            case "\u2705":
                validationMessage += "Acceptée par ";
                member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Votre candidature a été acceptée ! Bienvenue sur GravenDev").queue());
                GuildUtils.addRole(member, String.valueOf(memberRoleId)).queue();
                color = Color.decode("#008000");
                break;

            case "\u274C":
                validationMessage += "Refusée par ";
                member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Votre candidature a été refusée").queue());
                break;

            default:
                event.getReaction().removeReaction(event.getUser()).queue();
                return null;

        }

        validationMessage += event.getUser().getAsTag();

        return new EmbedBuilder(message.getEmbeds().get(0))
                .setTitle(validationMessage)
                .setColor(color)
                .build();
    }

}
