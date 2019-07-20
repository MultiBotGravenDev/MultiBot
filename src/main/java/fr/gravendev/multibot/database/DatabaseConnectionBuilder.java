package fr.gravendev.multibot.database;

public class DatabaseConnectionBuilder {

    public String host;
    public String user;
    public String password;
    public String database;

    private DatabaseConnectionBuilder() {}

    public static DatabaseConnectionBuilder aDatabaseConnection() {
        return new DatabaseConnectionBuilder();
    }

    public DatabaseConnectionBuilder withhost(String host) {
        this.host = host;
        return this;
    }

    public DatabaseConnectionBuilder withuser(String user) {
        this.user = user;
        return this;
    }

    public DatabaseConnectionBuilder withpassword(String password) {
        this.password = password;
        return this;
    }

    public DatabaseConnectionBuilder withdatabase(String database) {
        this.database = database;
        return this;
    }

    public DatabaseConnection build() {
        return new DatabaseConnection(this);
    }

}
