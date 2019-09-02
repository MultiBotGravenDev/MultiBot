package fr.gravendev.multibot.database.data;

import fr.gravendev.multibot.votes.Vote;
import fr.gravendev.multibot.votes.VoteType;

import java.util.List;
import java.util.stream.Collectors;

public class VoteData {

    private final int voteId;
    private final long userId, messageId;
    private final String role;
    private final boolean accepted;
    private List<Vote> votes;

    VoteData(VoteDataBuilder builder) {
        this.voteId = builder.getVoteId();
        this.userId = builder.getUserId();
        this.messageId = builder.getMessageId();
        this.role = builder.getRole();
        this.accepted = builder.isAccepted();
        this.votes = builder.getVotes();
    }

    public int getVoteId() {
        return voteId;
    }

    public long getMessageId() {
        return messageId;
    }

    public String getRole() {
        return role;
    }

    public long getUserId() {
        return userId;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public List<Long> getVotersByType(VoteType voteType) {
        return votes.stream()
                .filter(vote -> vote.getType() == voteType)
                .map(Vote::getUserId)
                .collect(Collectors.toList());
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void removeVote(Vote vote) {
        votes.remove(vote);
    }
}
