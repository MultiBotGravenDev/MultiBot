package fr.gravendev.multibot.tasks.antiroles;

import fr.gravendev.multibot.database.DatabaseConnection;

public class AntiReview extends AntiRole {

    public AntiReview(DatabaseConnection databaseConnection) {
        super(databaseConnection, "anti-review");
    }

}
