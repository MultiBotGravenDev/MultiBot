package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.data.GuildIdsData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildIdDAO extends DAO<GuildIdsData> {

    public GuildIdDAO(Connection connection) {
        super(connection);
    }

    @Override
    public boolean save(GuildIdsData guildIdsData) {

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO guild_id VALUES(?, ?) ON DUPLICATE KEY UPDATE id = ?");
            preparedStatement.setString(1, guildIdsData.name);
            preparedStatement.setString(2, guildIdsData.id + "");
            preparedStatement.setString(3, guildIdsData.id + "");

            preparedStatement.execute();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public GuildIdsData get(String value) {
        GuildIdsData guildIdsData = null;

        try {
            PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM guild_id WHERE name = ?");
            statement.setString(1, value);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                long id = Long.parseLong(resultSet.getString("id"));
                guildIdsData = new GuildIdsData(name, id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return guildIdsData;
    }

}
