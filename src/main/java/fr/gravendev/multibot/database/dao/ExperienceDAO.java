package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.data.ExperienceData;
import fr.gravendev.multibot.database.DAO;

import java.sql.*;
import java.util.Date;

public class ExperienceDAO extends DAO<ExperienceData> {

    public ExperienceDAO(Connection connection) {
        super(connection);
    }

    @Override
    public boolean save(ExperienceData data) {
        try (PreparedStatement statement = this.getConnection().prepareStatement(
               "INSERT INTO experience(`discord_id`, `experience`, `level`, `messages_count`, `last_message`) " +
                       "VALUES (?, ?, ?, ?, NOW()) ON DUPLICATE KEY UPDATE " +
                    "experience = VALUES(experience), level = VALUES(level), messages_count = VALUES(messages_count), last_message = VALUES(last_message)")) {

            statement.setString(1, data.getDiscordID());
            statement.setInt(2, data.getExperiences());
            statement.setInt(3, data.getLevels());
            statement.setInt(4, data.getMessages());

            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ExperienceData get(String discordID) {
        ExperienceData data = null;
        try (PreparedStatement statement = this.getConnection().prepareStatement("SELECT * FROM experience WHERE discord_id = ?")) {
            statement.setString(1, discordID);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int experience = resultSet.getInt("experience");
                int level = resultSet.getInt("level");
                int message = resultSet.getInt("messages_count");
                Date lastMessage = resultSet.getTimestamp("last_message");
                data = new ExperienceData(discordID, experience, level, message, lastMessage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public void delete(ExperienceData obj) {}
}
