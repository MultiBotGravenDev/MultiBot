package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.data.BadWordsData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BadWordsDAO extends DAO<BadWordsData> {

    public BadWordsDAO(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    @Override
    protected boolean save(BadWordsData obj, Connection connection) throws SQLException {

        connection.prepareStatement("TRUNCATE TABLE bad_words").execute();

        for (String badWord : obj.getBadWords().split(" ")) {

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO bad_words(word) VALUE(?)");
            preparedStatement.setString(1, badWord);

            preparedStatement.execute();

        }

        return true;
    }

    @Override
    protected BadWordsData get(String value, Connection connection) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bad_words");
        ResultSet result = preparedStatement.executeQuery();

        StringBuilder badWords = new StringBuilder();

        while (result.next()) {
            badWords.append(result.getString("word"));
            badWords.append(" ");
        }

        return new BadWordsData(badWords.toString());
    }

    @Override
    protected void delete(BadWordsData obj, Connection connection) throws SQLException {

        BadWordsData badWords = this.get("", connection);
        String words = badWords.getBadWords();
        // Error ? words.replace(words, "")
        BadWordsData finalBadWords = new BadWordsData(words.replace(words, ""));

        this.save(finalBadWords, connection);

    }

}
