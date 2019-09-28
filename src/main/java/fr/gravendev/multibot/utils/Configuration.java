package fr.gravendev.multibot.utils;

public enum Configuration {

    TOKEN("", "bot.token"),
    PREFIX("", "bot.prefix"),

    DB_HOST("", "database.host"),
    DB_USERNAME("", "database.username"),
    DB_PASSWORD("", "database.password"),
    DB_DATABASE("", "database.database"),

    GUILD("", "identifiers.guild"),

    ANTI_IMAGE("", "identifiers.roles.anti_image"),
    ANTI_MEME("", "identifiers.roles.anti_meme"),
    ANTI_REPOST("", "identifiers.roles.anti_repost"),
    ANTI_REVIEW("", "identifiers.roles.anti_review"),
    DEVELOPPEUR("", "identifiers.roles.developpeur"),
    HONORABLE("", "identifiers.roles.honorable"),
    MEMBER("", "identifiers.roles.member"),
    MUTED("", "identifiers.roles.muted"),
    PILIER("", "identifiers.roles.pilier"),

    CANDIDS("", "identifiers.channels.candids"),
    LOGS("", "identifiers.channels.logs"),
    RULES("", "identifiers.channels.rules"),
    READ_THIS_SALOON("", "identifiers.channels.read_this_saloon"),
    SONDAGES("", "identifiers.channels.sondages"),
    SONDAGES_VERIF("", "identifiers.channels.sondages_verif"),
    PROJECTS("", "identifiers.channels.projects"),
    PROJECTS_MINECRAFT("", "identifiers.channels.projects_minecraft");

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

    public static Configuration getConfigByName(String name) {
        for (Configuration value : values()) {
            if(value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }

}
