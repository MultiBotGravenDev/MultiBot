package fr.gravendev.multibot.database.data;

public class MessageData {
    private final String id;
    private final String message;

    public MessageData(String id, String message) {
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
