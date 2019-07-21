package fr.gravendev.multibot.moderation;

public enum InfractionType {

    BAN("Ban"),
    MUTE("Mute"),
    KICK("Kick"),
    WARN("Avertissement");

    private String infraction;

    InfractionType(String infraction) {
        this.infraction = infraction;
    }

    public String getInfraction() {
        return infraction;
    }
}
