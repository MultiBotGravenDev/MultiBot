package fr.gravendev.multibot.tasks.antiroles;

import fr.gravendev.multibot.database.DatabaseConnection;

public class AntiRepost extends AntiRole {

    public AntiRepost(DatabaseConnection databaseConnection) {
        super(databaseConnection, "anti-repost");
    }

}
