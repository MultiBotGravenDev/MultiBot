package fr.gravendev.multibot.data;

import java.util.Date;

public class ExperienceData {

    private final String discordID;
    private int experience, level, message;
    private Date lastMessage;

    public ExperienceData(String discordID, int experience, int level, int message, Date lastMessage) {
        this.discordID = discordID;
        this.experience = experience;
        this.level = level;
        this.message = message;
        this.lastMessage = lastMessage;
    }

    public ExperienceData(String discordID) {
        this.discordID = discordID;
        this.experience = 0;
        this.level = 0;
        this.message = 0;
        this.lastMessage = new Date(0);
    }

    public String getDiscordID() {
        return discordID;
    }

    public int getExperiences() {
        return experience;
    }

    public int getLevels() {
        return level;
    }

    public int getMessages() {
        return message;
    }

    public Date getLastMessage() {
        return lastMessage;
    }

    public void setExperiences(int experiences) {
        this.experience = experiences;
    }

    public void setLastMessage(Date lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setLevels(int levels) {
        this.level = levels;
    }

    public void setMessages(int messages) {
        this.message = messages;
    }
}
