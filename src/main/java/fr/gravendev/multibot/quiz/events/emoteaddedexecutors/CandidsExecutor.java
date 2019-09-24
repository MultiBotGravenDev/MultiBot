package fr.gravendev.multibot.quiz.events.emoteaddedexecutors;

import fr.gravendev.multibot.utils.Configuration;
import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
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

            Member member = message.getMentionedMembers().get(0);
            String validationMessage = member.getAsMention() + "\n\n";
            Color color = Color.getColor("0xFF0025");

            switch (event.getReactionEmote().getName()) {

                case "\u2705":
                    validationMessage += "Acceptée par ";
                    member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Votre candidature a été acceptée ! Bienvenue sur GravenDev").queue());
                    GuildUtils.addRole(member, String.valueOf(memberRoleId)).queue();
                    color = Color.getColor("0x008000");
                    break;

                case "\u274C":
                    validationMessage += "Refusée par ";
                    member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Votre candidature a été refusée").queue());
                    break;

                default:
                    event.getReaction().removeReaction(event.getUser()).queue();
                    return;

            }

            validationMessage += event.getMember().getAsMention();

            message.clearReactions().queue();

            message.editMessage(new EmbedBuilder(message.getEmbeds().get(0))
                    .setTitle(member.getAsMention() + ". " + validationMessage)
                    .setColor(color)
                    .build()).queue();

        });

    }

}
