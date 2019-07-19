package fr.gravendev.multibot.system.antiroles;

import fr.gravendev.multibot.database.DatabaseConnection;

public class AntiRepost extends AntiRole {

    public AntiRepost(DatabaseConnection databaseConnection) {
        super(databaseConnection, "anti_repost");
    }

}
