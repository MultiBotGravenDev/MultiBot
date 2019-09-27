package fr.gravendev.multibot.votes;

public enum VoteType {
    YES("Oui", "\u2705"),
    NO("Non", "\u274C"),
    WHITE("Blanc", "\u2B1C");

    private final String name;
    private final String reaction;

    VoteType(String name, String reaction) {
        this.name = name;
        this.reaction = reaction;
    }

    public String getName() {
        return name;
    }

    public String getReaction() {
        return reaction;
    }

    // TODO Maybe that can be refactored using a stream filter (?)
    public static VoteType getVoteTypeByReaction(String reaction) {
        for (VoteType voteType : values()) {
            if (voteType.getReaction().equals(reaction)) {
                return voteType;
            }
        }
        return null;
    }
}
