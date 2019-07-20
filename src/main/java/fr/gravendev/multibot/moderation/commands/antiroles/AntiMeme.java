package fr.gravendev.multibot.moderation.commands.antiroles;

import fr.gravendev.multibot.database.DatabaseConnection;

public class AntiMeme extends AntiRole {

    public AntiMeme(DatabaseConnection databaseConnection) {
        super(databaseConnection, "anti_meme");
    }

}
