package fr.gravendev.multibot.tasks.antiroles;

import fr.gravendev.multibot.database.DatabaseConnection;

public class AntiImage extends AntiRole {

    public AntiImage(DatabaseConnection databaseConnection) {
        super(databaseConnection, "anti_image");
    }

}
