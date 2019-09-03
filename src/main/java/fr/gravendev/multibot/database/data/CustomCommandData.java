package fr.gravendev.multibot.database.data;

public class CustomCommandData {

    private final String command;
    private final String text;

    public CustomCommandData(String command, String text) {
        this.command = command;
        this.text = text;
    }

    public String getCommand() {
        return command;
    }

    public String getText() {
        return text;
    }
}
