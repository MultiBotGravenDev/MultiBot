package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.data.VoteData;
import fr.gravendev.multibot.database.data.VoteDataBuilder;
import fr.gravendev.multibot.utils.PreparedStatementBuilder;
import fr.gravendev.multibot.votes.Vote;
import fr.gravendev.multibot.votes.VoteType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

// TODO Refactor
public class VoteDAO extends DAO<VoteData> {
    public VoteDAO(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    @Override
    public boolean save(VoteData voteData, Connection connection) throws SQLException {
        PreparedStatementBuilder statementBuilder = new PreparedStatementBuilder(connection);
        if (get(String.valueOf(voteData.getMessageId()), connection).getRole() == null) {
            statementBuilder
                    .prepareStatement("INSERT INTO votes (message_id, role, user_id, accepted) VALUES(?, ?, ?, ?)")
                    .setString(String.valueOf(voteData.getMessageId()))
                    .setString(String.valueOf(voteData.getRole()))
                    .setString(String.valueOf(voteData.getUserId()))
                    .setBoolean(voteData.isAccepted())
                    .execute();
        }

        if (get(String.valueOf(voteData.getMessageId())).isAccepted() != voteData.isAccepted()) {
            statementBuilder
                    .prepareStatement("UPDATE votes SET accepted = ? WHERE message_id = ?")
                    .setBoolean(voteData.isAccepted())
                    .setString(String.valueOf(voteData.isAccepted()))
                    .execute();
        }

        for (Vote vote : voteData.getVotes()) {
            VoteType type = vote.getType();
            long voter = vote.getUserId();
            String typeName = type.name().toLowerCase();

            statementBuilder
                    .prepareStatement("INSERT INTO voters VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE choice = ?")
                    .setInt(voteData.getVoteId())
                    .setString(String.valueOf(voter))
                    .setString(typeName)
                    .setString(typeName)
                    .execute();
        }
        return false;
    }

    @Override
    public VoteData get(String value, Connection connection) throws SQLException {
        ResultSet resultSet = new PreparedStatementBuilder(connection)
                .prepareStatement("SELECT * FROM votes LEFT JOIN voters ON votes.id = voters.id WHERE message_id = ? OR user_id = ?")
                .setString(value)
                .setString(value)
                .executeQuery();
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
            VoteType voteType = VoteType.valueOf(choice.toUpperCase());

            voteDataBuilder.addVote(new Vote(voterId, voteType));
        }
        return voteDataBuilder.build();
    }

    @Override
    protected void delete(VoteData obj, Connection connection) {

    }
}
