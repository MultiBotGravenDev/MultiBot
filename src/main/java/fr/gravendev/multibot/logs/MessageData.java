package fr.gravendev.multibot.logs;

public class MessageData {

    private String discord_id, message_id, content;
    private long creation;

    MessageData(net.dv8tion.jda.api.entities.Message message) {
        this.discord_id = message.getAuthor().getId();
        this.message_id = message.getId();
        this.content = message.getContentDisplay();
        this.creation = message.getTimeCreated().toInstant().toEpochMilli();
    }

    public MessageData(String discord_id, String message_id, String content, long creation) {
        this.discord_id = discord_id;
        this.message_id = message_id;
        this.content = content;
        this.creation = creation;
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
