package fr.gravendev.multibot.moderation.commands.antiroles;

import fr.gravendev.multibot.database.DatabaseConnection;

public class AntiReview extends AntiRole {

    public AntiReview(DatabaseConnection databaseConnection) {
        super(databaseConnection, "anti_review");
    }

}
