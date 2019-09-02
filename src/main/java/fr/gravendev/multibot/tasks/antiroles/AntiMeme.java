package fr.gravendev.multibot.tasks.antiroles;

import fr.gravendev.multibot.database.DatabaseConnection;

public class AntiMeme extends AntiRole {

    public AntiMeme(DatabaseConnection databaseConnection) {
        super(databaseConnection, "anti-meme");
    }

}
