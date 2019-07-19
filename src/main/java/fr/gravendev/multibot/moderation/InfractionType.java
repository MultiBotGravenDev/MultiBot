package fr.gravendev.multibot.moderation;

public enum InfractionType {

    BAN("Ban"),
    MUTE("Mute"),
    TEMPBAN("Tempban"),
    TEMPMUTE("Tempmute"),
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
