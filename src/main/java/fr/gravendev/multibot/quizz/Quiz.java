package fr.gravendev.multibot.quizz;

import fr.gravendev.multibot.database.DatabaseConnection;

public class Quiz {

    private final DatabaseConnection databaseConnection;

    public Quiz(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

}
