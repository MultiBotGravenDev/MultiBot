package fr.gravendev.multibot.votes.events;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.VoteDAO;
import fr.gravendev.multibot.database.data.VoteData;
import fr.gravendev.multibot.database.data.VoteDataBuilder;
import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.votes.Vote;
import fr.gravendev.multibot.votes.VoteType;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EmoteAddedListener implements Listener<MessageReactionAddEvent> {

    private final VoteDAO voteDAO;

    public EmoteAddedListener(DatabaseConnection databaseConnection) {
        voteDAO = new VoteDAO(databaseConnection);
    }

    @Override
    public Class<MessageReactionAddEvent> getEventClass() {
        return MessageReactionAddEvent.class;
    }

    // TODO Refactor this in multiples methods
    @Override
    public void executeListener(MessageReactionAddEvent event) {
        User user = event.getUser();
        MessageChannel channel = event.getChannel();
        List<String> staffRoles = Arrays.asList("vote-honorable", "vote-developpeur", "votes-piliers");
        if (user.isBot()){
            return;
        }

        // TODO Check with Luka if staffRoles is a great name for this variable
        if (!staffRoles.contains(channel.getName())){
            return;
        }
        event.getReaction().removeReaction(user).queue();

        if (channel.retrieveMessageById(event.getMessageIdLong()).complete().getTimeCreated().isBefore(OffsetDateTime.now().minusDays(1))){
            return;
        }

        VoteData voteData = voteDAO.get(event.getMessageId());
        if (voteData == null){
            return;
        }

        long userId = user.getIdLong();
        List<Vote> removeVote = new ArrayList<>();
        voteData.getVotes().stream().filter(vote -> vote.getUserId() == userId)
                .forEach(removeVote::add);
        voteData.getVotes().removeAll(removeVote);

        VoteDataBuilder voteDataBuilder = VoteDataBuilder.fromVoteData(voteData);
        MessageReaction.ReactionEmote reactionEmote = event.getReactionEmote();
        VoteType voteType = VoteType.getVoteTypeByReaction(reactionEmote.getName());
        if(voteType == null)
            return;

        voteDataBuilder.addVote(new Vote(userId, voteType));

        voteDAO.save(voteDataBuilder.build());

        channel.sendMessage(user.getAsMention() + ", votre vote a bien été pris en compte")
                .queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));

    }

}
