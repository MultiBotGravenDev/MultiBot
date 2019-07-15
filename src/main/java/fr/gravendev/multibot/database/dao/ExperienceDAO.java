package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.data.ExperienceData;
import fr.gravendev.multibot.database.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExperienceDAO extends DAO<ExperienceData> {

    public ExperienceDAO(Connection connection) {
        super(connection);
    }

    @Override
    public boolean create(ExperienceData data) {
        try (PreparedStatement statement = this.getConnection().prepareStatement(
                "INSERT INTO experience (discord_id, experience, level, message, last_message) VALUES (?, ?, ?, ?, ?)")) {

            statement.setString(1, data.getDiscordID());
            statement.setInt(2, data.getExperiences());
            statement.setInt(3, data.getLevels());
            statement.setInt(4, data.getMessages());
            statement.setLong(5, data.getLastMessage());

            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(ExperienceData data) {
        try (PreparedStatement statement = this.getConnection().prepareStatement(
                "UPDATE clients SET experience = ?, level = ?, message = ?, last_message = ? WHERE discord_id = ?;")){

            statement.setInt(1, data.getExperiences());
            statement.setInt(2, data.getLevels());
            statement.setInt(3, data.getMessages());
            statement.setLong(4, data.getLastMessage());
            statement.setString(5, data.getDiscordID());

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
                int message = resultSet.getInt("message");
                long lastMessage = resultSet.getLong("last_message");

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
