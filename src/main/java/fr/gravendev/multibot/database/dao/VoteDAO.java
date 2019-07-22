package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.data.VoteData;
import fr.gravendev.multibot.database.data.VoteDataBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VoteDAO extends DAO<VoteData> {

    public VoteDAO(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    @Override
    public boolean save(VoteData voteData, Connection connection) throws SQLException {

        if (get(String.valueOf(voteData.messageId), connection).role == null) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO votes (message_id, role, user_id, accepted) VALUES(?, ?, ?, ?)");

            preparedStatement.setString(1, String.valueOf(voteData.messageId));
            preparedStatement.setString(2, String.valueOf(voteData.role));
            preparedStatement.setString(3, String.valueOf(voteData.userId));
            preparedStatement.setBoolean(4, voteData.accepted);

            preparedStatement.execute();
        }

        if (get(String.valueOf(voteData.messageId)).accepted != voteData.accepted) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE votes SET accepted = ? WHERE message_id = ?");

            preparedStatement.setBoolean(1, voteData.accepted);
            preparedStatement.setString(2, String.valueOf(voteData.messageId));

            preparedStatement.execute();
        }

        for (Long voter : voteData.yes) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO voters VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE choice = ?");
            preparedStatement.setInt(1, voteData.voteId);
            preparedStatement.setString(2, String.valueOf(voter));
            preparedStatement.setString(3, "yes");

            preparedStatement.setString(4, "yes");

            preparedStatement.execute();
        }

        for (Long voter : voteData.no) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO voters VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE choice = ?");
            preparedStatement.setInt(1, voteData.voteId);
            preparedStatement.setString(2, String.valueOf(voter));
            preparedStatement.setString(3, "no");

            preparedStatement.setString(4, "no");

            preparedStatement.execute();
        }

        for (Long voter : voteData.white) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO voters VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE choice = ?");
            preparedStatement.setInt(1, voteData.voteId);
            preparedStatement.setString(2, String.valueOf(voter));
            preparedStatement.setString(3, "white");

            preparedStatement.setString(4, "white");

            preparedStatement.execute();
        }
        return false;
    }

    @Override
    public VoteData get(String value, Connection connection) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM votes LEFT JOIN voters ON votes.id = voters.id WHERE message_id = ? OR user_id = ?");
        preparedStatement.setString(1, value);
        preparedStatement.setString(2, value);

        ResultSet resultSet = preparedStatement.executeQuery();

        VoteDataBuilder voteDataBuilder = VoteDataBuilder.aVoteData();

        while (resultSet.next()) {

            int voteId = resultSet.getInt("id");
            long messageId = Long.parseLong(resultSet.getString("message_id"));
            String role = resultSet.getString("role");
            long userId = Long.parseLong(resultSet.getString("user_id"));
            boolean accepted = resultSet.getBoolean("accepted");

            voteDataBuilder
                    .withVoteId(voteId)
                    .withMessageId(messageId)
                    .withRole(role)
                    .withUserID(userId)
                    .isAccepted(accepted);

            long voterId;

            try {
                voterId = Long.parseLong(resultSet.getString("voter_id"));
            } catch (NumberFormatException ignored) {
                continue;
            }

            String choice = resultSet.getString("choice");

            switch (choice) {

                case "yes":
                    voteDataBuilder.addYes(voterId);
                    break;

                case "no":
                    voteDataBuilder.addNo(voterId);
                    break;

                case "white":
                    voteDataBuilder.addWhite(voterId);
                    break;

            }

        }

        return voteDataBuilder.build();
    }

    @Override
    protected void delete(VoteData obj, Connection connection) {

    }

}
