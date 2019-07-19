package fr.gravendev.multibot.votes.events;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.VoteDAO;
import fr.gravendev.multibot.database.data.VoteData;
import fr.gravendev.multibot.database.data.VoteDataBuilder;
import fr.gravendev.multibot.events.Listener;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class EmoteAddedListener implements Listener<MessageReactionAddEvent> {

    private final DatabaseConnection databaseConnection;

    public EmoteAddedListener(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Class<MessageReactionAddEvent> getEventClass() {
        return MessageReactionAddEvent.class;
    }

    @Override
    public void executeListener(MessageReactionAddEvent event) {

        if (event.getUser().isBot()) return;

        if (!Arrays.asList("vote-honorable", "vote-developpeur", "votes-piliers").contains(event.getChannel().getName())) return;
        event.getReaction().removeReaction(event.getUser()).queue();

        if (event.getChannel().getMessageById(event.getMessageIdLong()).complete().getCreationTime().isBefore(OffsetDateTime.now().minusDays(1))) return;

        try {
            VoteDAO voteDAO = new VoteDAO(this.databaseConnection);

            VoteData voteData = voteDAO.get(event.getMessageId());

            if (voteData == null) return;

            VoteDataBuilder voteDataBuilder = VoteDataBuilder
                    .fromVoteData(voteData);

            long userId = event.getUser().getIdLong();
            switch (event.getReactionEmote().getName()) {

                case "\u2705":
                    voteDataBuilder.addYes(userId);
                    break;

                case "\u274C":
                    voteDataBuilder.addNo(userId);
                    break;

                case "\u2B1C":
                    voteDataBuilder.addWhite(userId);
                    break;

            }

            voteDAO.save(voteDataBuilder.build());

            event.getChannel().sendMessage(event.getUser().getAsMention() + ", votre vote a bien été pris en compte")
                    .queue(message -> message.delete().queueAfter(5, TimeUnit.SECONDS));

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
