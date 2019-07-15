package fr.gravendev.multibot.json;

import java.io.File;

public class Configuration {

    public static final File CONFIGURATION_FILE = new File("configuration.json");

    private String token;
    private String host, username, password, database;
    private char prefix;

    public String getToken() {
        return this.token;
    }

    public char getPrefix() {
        return prefix;
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }
}
