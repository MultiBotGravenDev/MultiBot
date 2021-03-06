package fr.gravendev.multibot.polls.events;

import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.polls.PollsManager;
import fr.gravendev.multibot.utils.Configuration;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class EmoteAddedListener implements Listener<MessageReactionAddEvent> {

    private final PollsManager pollsManager;

    public EmoteAddedListener(PollsManager pollsManager) {
        this.pollsManager = pollsManager;
    }

    @Override
    public Class<MessageReactionAddEvent> getEventClass() {
        return MessageReactionAddEvent.class;
    }

    @Override
    public void executeListener(MessageReactionAddEvent event) {

        String pollVerifChannelId = Configuration.SONDAGES_VERIF.getValue();
        if (event.getUser().isBot() || !event.getChannel().getId().equals(pollVerifChannelId)) return;

        event.getChannel().retrieveMessageById(event.getMessageIdLong()).queue(message -> {

            Member member = message.getMentionedMembers().get(0);
            String validationMessage = member.getAsMention() + "\n\n";

            switch (event.getReactionEmote().getName()) {

                case "\u2705":
                    validationMessage += ":white_check_mark: accepté ";
                    this.pollsManager.send(member.getUser(), message.getEmbeds().get(0).getTitle());
                    this.pollsManager.removePoll(event.getUser());
                    member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Votre sondage a été accepté").queue());
                    break;

                case "\u274C":
                    validationMessage += ":x: refusé ";
                    this.pollsManager.removePoll(event.getUser());
                    member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Votre sondage a été refusé").queue());
                    break;

                default:
                    event.getReaction().removeReaction(event.getUser()).queue();
                    return;

            }

            validationMessage += "par " + event.getMember().getAsMention();

            message.clearReactions().queue();

            message.editMessage(new MessageBuilder(message)
                    .setContent(validationMessage)
                    .build()).queue();

        });

    }

}
