package fr.gravendev.multibot.logs;

public class Message {

    private String discord_id, message_id, content;
    private long creation;


    public Message(net.dv8tion.jda.core.entities.Message message) {
        this.discord_id = message.getAuthor().getId();
        this.message_id = message.getId();
        this.content = message.getContentDisplay();
        this.creation = message.getCreationTime().toInstant().toEpochMilli();
    }

    public String getDiscordID() {
        return discord_id;
    }

    public String getMessage_id() {
        return message_id;
    }

    public String getContent() {
        return content;
    }

    public long getCreation() {
        return creation;
    }
}
