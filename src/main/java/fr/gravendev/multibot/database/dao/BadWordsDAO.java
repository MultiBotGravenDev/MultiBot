package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.data.BadWordsData;
import fr.gravendev.multibot.utils.PreparedStatementBuilder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BadWordsDAO extends DAO<BadWordsData> {
    public BadWordsDAO(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    @Override
    protected boolean save(BadWordsData obj, Connection connection) throws SQLException {
        PreparedStatementBuilder statementBuilder = new PreparedStatementBuilder(connection);

        statementBuilder
                .prepareStatement("TRUNCATE TABLE bad_words")
                .execute();
        for (String badWord : obj.getBadWords().split(" ")) {
            statementBuilder.prepareStatement("INSERT INTO bad_words(word) VALUE(?)")
                    .setString(badWord)
                    .execute();
        }
        return true;
    }

    @Override
    protected BadWordsData get(String value, Connection connection) throws SQLException {
        ResultSet result = new PreparedStatementBuilder(connection)
                .prepareStatement("SELECT * FROM bad_words")
                .executeQuery();
        StringBuilder badWords = new StringBuilder();

        while (result.next()) {
            badWords.append(result.getString("word"))
                    .append(" ");
        }
        return new BadWordsData(badWords.toString());
    }

    @Override
    protected void delete(BadWordsData obj, Connection connection) throws SQLException {
        BadWordsData badWords = this.get("", connection);
        String words = badWords.getBadWords();
        BadWordsData finalBadWords = new BadWordsData(words.replace(obj.getBadWords(), ""));

        this.save(finalBadWords, connection);
    }
}
