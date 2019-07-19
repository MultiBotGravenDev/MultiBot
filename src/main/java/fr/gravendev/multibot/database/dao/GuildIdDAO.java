package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.data.GuildIdsData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildIdDAO extends DAO<GuildIdsData> {

    public GuildIdDAO(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    @Override
    protected GuildIdsData get(String value, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM guild_id WHERE name = ?");
        statement.setString(1, value);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            String name = resultSet.getString("name");
            long id = Long.parseLong(resultSet.getString("id"));
            return new GuildIdsData(name, id);
        }
        
        return null;
    }
    
    @Override
    public boolean save(GuildIdsData guildIdsData, Connection connection) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO guild_id VALUES(?, ?) ON DUPLICATE KEY UPDATE id = ?");
        preparedStatement.setString(1, guildIdsData.name);
        preparedStatement.setString(2, guildIdsData.id + "");
        preparedStatement.setString(3, guildIdsData.id + "");

        preparedStatement.execute();

        return true;
    }
    

    @Override
    protected void delete(GuildIdsData obj, Connection connection) {
    }
}
