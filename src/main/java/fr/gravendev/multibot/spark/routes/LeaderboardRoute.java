package fr.gravendev.multibot.spark.routes;

import fr.gravendev.multibot.database.dao.ExperienceDAO;
import net.dv8tion.jda.api.entities.Guild;
import spark.Request;
import spark.Response;
import spark.Route;

public class LeaderboardRoute implements Route {

    private final ExperienceDAO experienceDAO;
    private final Guild guild;

    public LeaderboardRoute(Guild guild, ExperienceDAO experienceDAO) {
        this.guild = guild;
        this.experienceDAO = experienceDAO;
    }

    @Override
    public Object handle(Request request, Response response) {
        return experienceDAO.getALL(guild);
    }
}
