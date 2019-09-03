package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.data.ImmunisedIdsData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ImmunisedIdDAO extends DAO<ImmunisedIdsData> {

    public ImmunisedIdDAO(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    @Override
    protected boolean save(ImmunisedIdsData obj, Connection connection) throws SQLException {

        connection.prepareStatement("TRUNCATE TABLE immunised_ids").execute();

        for (Long immunisedId : obj.immunisedIds) {

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO immunised_ids(id) VALUE(?)");
            preparedStatement.setString(1, String.valueOf(immunisedId));

            preparedStatement.execute();

        }

        return false;
    }

    @Override
    protected ImmunisedIdsData get(String value, Connection connection) throws SQLException {

        List<Long> ids = new ArrayList<>();

        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM immunised_ids");
        ResultSet result = preparedStatement.executeQuery();

        while (result.next()) {

            ids.add(Long.valueOf(result.getString("id")));

        }

        return new ImmunisedIdsData(ids);
    }

    @Override
    protected void delete(ImmunisedIdsData obj, Connection connection) throws SQLException {

        ImmunisedIdsData immunisedIdsData = this.get("", connection);
        List<Long> ids = immunisedIdsData.immunisedIds;
        ids.removeAll(obj.immunisedIds);
        ImmunisedIdsData finalBadWords = new ImmunisedIdsData(ids);

        this.save(finalBadWords, connection);
    }

}
