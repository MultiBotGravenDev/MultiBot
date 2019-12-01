package fr.gravendev.multibot.spark;

import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.spark.routes.LeaderboardRoute;
import fr.gravendev.multibot.spark.routes.MainRoute;
import fr.gravendev.multibot.spark.routes.auth.CallbackRoute;
import fr.gravendev.multibot.spark.routes.auth.LoginRoute;
import fr.gravendev.multibot.spark.routes.auth.UserRoute;
import fr.gravendev.multibot.utils.Configuration;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import spark.Filter;
import spark.Route;

import static spark.Spark.*;

public class SparkAPI {

    private Guild guild;
    private DAOManager daoManager;

    public SparkAPI(JDA jda, DAOManager daoManager) throws InterruptedException {
        this.daoManager = daoManager;
        jda.awaitReady();
        this.guild = jda.getGuildById(Configuration.GUILD.getValue());

        port(8080);
        options("/*", getRouteOptions());
        before(getFilter());
    }

    public void initRoutes() {

        path("/api/v1", () -> {

            path("/auth", () -> {
                get("/login", new LoginRoute());
                get("/callback", new CallbackRoute());
            });

            get("/user", new UserRoute(guild));
            get("/main", new MainRoute(guild, daoManager));
            get("/leaderboard", new LeaderboardRoute(guild, daoManager.getExperienceDAO()));
        });
    }

    private Route getRouteOptions() {
        return (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        };
    }

    private Filter getFilter() {
        return (request, response) -> {
            response.header("Access-Control-Allow-Credentials", "true");
            response.header("Access-Control-Allow-Origin", "https://multibot.fr");
        };
    }

}
