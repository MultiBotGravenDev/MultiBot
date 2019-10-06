package fr.gravendev.multibot.database.dao;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.data.ImmunisedIdsData;
import fr.gravendev.multibot.utils.PreparedStatementBuilder;

import java.sql.Connection;
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
        PreparedStatementBuilder statementBuilder = new PreparedStatementBuilder(connection);

        statementBuilder
                .prepareStatement("TRUNCATE TABLE immunised_ids")
                .execute();
        for (Long immunisedId : obj.immunisedIds) {
            statementBuilder
                    .prepareStatement("INSERT INTO immunised_ids(id) VALUE(?)")
                    .setString(String.valueOf(immunisedId))
                    .execute();
        }
        return false; // TODO Wtf??
    }

    @Override
    protected ImmunisedIdsData get(String value, Connection connection) throws SQLException {
        List<Long> ids = new ArrayList<>();
        ResultSet result = new PreparedStatementBuilder(connection)
                .prepareStatement("SELECT * FROM immunised_ids")
                .executeQuery();

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
