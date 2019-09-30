package fr.gravendev.multibot.votes;

public class Vote {

    private final long userId;
    private VoteType type;

    public Vote(long userId, VoteType type) {
        this.userId = userId;
        this.type = type;
    }

    public long getUserId() {
        return userId;
    }

    public VoteType getType() {
        return type;
    }

}
