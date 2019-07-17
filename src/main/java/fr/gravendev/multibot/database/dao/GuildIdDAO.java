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
    public GuildIdsData get(String value) {
        GuildIdsData guildIdsData = null;

        try {
            PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM guild_id WHERE name = ?");
            statement.setString(1, value);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                guildIdsData = new GuildIdsData(resultSet.getLong("id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return guildIdsData;
    }

}
