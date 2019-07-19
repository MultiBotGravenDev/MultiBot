package fr.gravendev.multibot.database.data;

import java.util.ArrayList;
import java.util.List;

public final class VoteDataBuilder {

    int voteId;
    long messageId;
    String role;
    long userId;
    boolean accepted;

    List<Long> yes = new ArrayList<>();
    List<Long> no = new ArrayList<>();
    List<Long> white = new ArrayList<>();

    private VoteDataBuilder() {
    }

    public static VoteDataBuilder aVoteData() {
        return new VoteDataBuilder();
    }

    public static VoteDataBuilder fromVoteData(VoteData voteData) {
        VoteDataBuilder voteDataBuilder = new VoteDataBuilder();

        voteDataBuilder.voteId = voteData.voteId;
        voteDataBuilder.messageId = voteData.messageId;
        voteDataBuilder.role = voteData.role;

        voteDataBuilder.yes = new ArrayList<>(voteData.yes);
        voteDataBuilder.no = new ArrayList<>(voteData.no);
        voteDataBuilder.white = new ArrayList<>(voteData.white);

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

    public void addYes(long yes) {
        this.yes.add(yes);
    }

    public void addNo(long no) {
        this.no.add(no);
    }

    public void addWhite(long white) {
        this.white.add(white);
    }

    public VoteData build() {
        return new VoteData(this);
    }

}
