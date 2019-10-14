package fr.gravendev.multibot.database.data;

import java.util.Date;

public class ExperienceData {
    private final String discordID;
    private final Date lastMessage;
    private int experiencePoints, level, messageCount;

    public ExperienceData(String discordID, int experiencePoints, int level, int message, Date lastMessage) {
        this.discordID = discordID;
        this.experiencePoints = experiencePoints;
        this.level = level;
        this.messageCount = message;
        this.lastMessage = lastMessage;
    }

    public ExperienceData(String discordID) {
        this.discordID = discordID;
        this.experiencePoints = 0;
        this.level = 0;
        this.messageCount = 0;
        this.lastMessage = new Date(0);
    }

    public String getDiscordID() {
        return discordID;
    }

    public int getExperiences() {
        return experiencePoints;
    }

    public int getLevels() {
        return level;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public Date getLastMessage() {
        return lastMessage;
    }

    public void incrementMessageCount() {
        messageCount++;
    }

    public void incrementLevel() {
        level++;
    }

    public void addExperiencePoints(int experiencePoints) {
        this.experiencePoints += experiencePoints;
    }

    public void removeExperiencePoints(int experiencePoints) {
        this.experiencePoints -= experiencePoints;
    }

    @Override
    public String toString() {
        return String.format("ID: %s / Experience: %s / Level: %s / Message: %s / LastMessage: %s", discordID, experiencePoints, level, messageCount, lastMessage);
    }
}
