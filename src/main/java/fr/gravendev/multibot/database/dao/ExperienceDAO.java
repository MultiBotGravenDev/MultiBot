package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.data.ExperienceData;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExperienceDAO extends DAO<ExperienceData> {

    public ExperienceDAO(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    @Override
    protected boolean save(ExperienceData data, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO experience(`discord_id`, `experience`, `level`, `messages_count`, `last_message`) " +
                        "VALUES (?, ?, ?, ?, NOW()) ON DUPLICATE KEY UPDATE " +
                        "experience = VALUES(experience), level = VALUES(level), messages_count = VALUES(messages_count), last_message = VALUES(last_message)");

        statement.setString(1, data.getDiscordID());
        statement.setInt(2, data.getExperiences());
        statement.setInt(3, data.getLevels());
        statement.setInt(4, data.getMessages());

        statement.executeUpdate();

        return true;
    }

    @Override
    protected ExperienceData get(String discordID, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM experience WHERE discord_id = ?");
        statement.setString(1, discordID);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            int experience = resultSet.getInt("experience");
            int level = resultSet.getInt("level");
            int message = resultSet.getInt("messages_count");
            Date lastMessage = resultSet.getTimestamp("last_message");
            return new ExperienceData(discordID, experience, level, message, lastMessage);
        }
        return null;
    }

    public List<JSONObject> getALL(Guild guild) {
        List<JSONObject> experienceData = new ArrayList<>();
        try (Connection connection = getConnection()) {

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM experience ORDER BY level DESC, experience DESC");

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String discord_id = resultSet.getString("discord_id");
                int experience = resultSet.getInt("experience");
                int level = resultSet.getInt("level");
                int messages = resultSet.getInt("messages_count");

                JSONObject jsonObject = new JSONObject();

                Member member = guild.getMemberById(discord_id);
                if(member == null) continue;

                jsonObject.put("name", member.getUser().getName());
                jsonObject.put("avatarURL", member.getUser().getAvatarUrl());
                jsonObject.put("messages", messages);
                jsonObject.put("experience", experience);
                jsonObject.put("level", level);

                experienceData.add(jsonObject);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return experienceData;
    }

    @Override
    protected void delete(ExperienceData obj, Connection connection) {

    }
}
