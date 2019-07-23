package fr.gravendev.multibot.polls.events;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.polls.PollsManager;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

public class EmoteAddedListener implements Listener<MessageReactionAddEvent> {

    private final GuildIdDAO guildIdDAO;
    private final PollsManager pollsManager;

    public EmoteAddedListener(DatabaseConnection databaseConnection, PollsManager pollsManager) {
        this.guildIdDAO = new GuildIdDAO(databaseConnection);
        this.pollsManager = pollsManager;
    }

    @Override
    public Class<MessageReactionAddEvent> getEventClass() {
        return MessageReactionAddEvent.class;
    }

    @Override
    public void executeListener(MessageReactionAddEvent event) {

        if (event.getUser().isBot()) return;

        long pollVerifChannelId = this.guildIdDAO.get("sondages_verif").id;

        if (event.getChannel().getIdLong() != pollVerifChannelId) return;

        event.getChannel().getMessageById(event.getMessageIdLong()).queue(message -> {

            Member member = message.getMentionedMembers().get(0);
            String validationMessage = member.getAsMention() + "\n\n";

            switch (event.getReactionEmote().getName()) {

                case "\u2705":
                    validationMessage += ":white_check_mark: accepté ";
                    this.pollsManager.sendFinalPoll(member.getUser(), message.getEmbeds().get(0).getTitle());
                    this.pollsManager.removePoll(event.getUser());
                    break;

                case "\u274C":
                    validationMessage += ":x: refusé ";
                    this.pollsManager.removePoll(event.getUser());
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
