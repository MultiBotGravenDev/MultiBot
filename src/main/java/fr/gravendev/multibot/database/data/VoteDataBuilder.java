package fr.gravendev.multibot.database.data;

import fr.gravendev.multibot.votes.Vote;

import java.util.ArrayList;
import java.util.List;

public final class VoteDataBuilder {
    private int voteId;
    private long userId, messageId;
    private String role;
    private boolean accepted;

    private List<Vote> votes = new ArrayList<>();

    private VoteDataBuilder() {
    }

    public static VoteDataBuilder aVoteData() {
        return new VoteDataBuilder();
    }

    public static VoteDataBuilder fromVoteData(VoteData voteData) {
        VoteDataBuilder voteDataBuilder = new VoteDataBuilder();
        voteDataBuilder.voteId = voteData.getVoteId();
        voteDataBuilder.userId = voteData.getUserId();
        voteDataBuilder.messageId = voteData.getMessageId();
        voteDataBuilder.role = voteData.getRole();
        voteDataBuilder.accepted = voteData.isAccepted();
        voteDataBuilder.votes = voteData.getVotes();

        return voteDataBuilder;
    }

    public VoteDataBuilder withVoteId(int voteId) {
        this.voteId = voteId;
        return this;
    }

    public VoteDataBuilder withMessageId(long messageId) {
        this.messageId = messageId;
        return this;
    }

    public VoteDataBuilder withRole(String role) {
        this.role = role;
        return this;
    }

    public VoteDataBuilder withUserID(long userId) {
        this.userId = userId;
        return this;
    }

    public VoteDataBuilder isAccepted(boolean accepted) {
        this.accepted = accepted;
        return this;
    }

    public void addVote(Vote vote) {
        this.votes.add(vote);
    }

    public VoteData build() {
        return new VoteData(this);
    }

    public int getVoteId() {
        return voteId;
    }

    public long getUserId() {
        return userId;
    }

    public long getMessageId() {
        return messageId;
    }

    public String getRole() {
        return role;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public List<Vote> getVotes() {
        return votes;
    }
}
