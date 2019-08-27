package fr.gravendev.multibot.spark.routes;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.ExperienceDAO;
import net.dv8tion.jda.api.entities.Guild;
import spark.Request;
import spark.Response;
import spark.Route;

public class LeaderboardRoute implements Route {
    private DatabaseConnection databaseConnection;
    private Guild guild;

    public LeaderboardRoute(DatabaseConnection databaseConnection, Guild guild) {
        this.databaseConnection = databaseConnection;
        this.guild = guild;
    }

    @Override
    public Object handle(Request request, Response response) {
        ExperienceDAO experienceDAO = new ExperienceDAO(databaseConnection);
        return experienceDAO.getALL(guild);
    }
}
