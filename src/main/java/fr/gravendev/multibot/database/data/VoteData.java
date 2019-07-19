package fr.gravendev.multibot.database.data;

import java.util.List;

public class VoteData {

    public final int voteId;
    public final long messageId;
    public final String role;
    public final long userId;
    public final boolean accepted;

    public final List<Long> yes;
    public final List<Long> no;
    public final List<Long> white;

    VoteData(VoteDataBuilder builder) {
        this.voteId = builder.voteId;
        this.messageId = builder.messageId;
        this.role = builder.role;
        this.yes = builder.yes;
        this.no = builder.no;
        this.white = builder.white;
        this.userId = builder.userId;
        this.accepted = builder.accepted;
    }

}
