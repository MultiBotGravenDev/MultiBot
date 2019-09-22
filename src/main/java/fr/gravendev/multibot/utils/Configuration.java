package fr.gravendev.multibot.utils;

public enum Configuration {

    TOKEN("", "bot.token"),
    PREFIX("", "bot.prefix"),

    DB_HOST("", "database.host"),
    DB_USERNAME("", "database.username"),
    DB_PASSWORD("", "database.password"),
    DB_DATABASE("", "database.database");

    private String value;
    private final String path;

    Configuration(String value, String path) {
        this.value = value;
        this.path = path;
    }

    public String getValue() {
        return value;
    }

    public String getPath() {
        return path;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
