package fr.gravendev.multibot.spark;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.spark.routes.IndexInfos;
import fr.gravendev.multibot.spark.routes.LeaderboardRoute;
import fr.gravendev.multibot.spark.routes.admin.AdminInfos;
import fr.gravendev.multibot.spark.routes.admin.IsAdminRoute;
import fr.gravendev.multibot.utils.Configuration;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import spark.Filter;

import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.path;

public class SparkAPI {

    private Guild guild;
    private DatabaseConnection databaseConnection;

    public SparkAPI(JDA jda, DatabaseConnection databaseConnection) throws InterruptedException {
        this.databaseConnection = databaseConnection;
        jda.awaitReady();
        this.guild = jda.getGuildById(Configuration.GUILD.getValue());
        after((Filter) (request, response) -> {
            response.header("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
            response.header("Access-Control-Allow-Origin", "http://127.0.0.1:8080");
            response.header("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
            response.header("Access-Control-Allow-Credentials", "true");
        });
    }

    public void initRoutes() {
        path("/api/v1", () -> {
            get("/indexInfos", new IndexInfos(guild, databaseConnection));
            get("/leaderboard", new LeaderboardRoute(databaseConnection, guild));

            path("/admin", () -> {
                get("/isAdmin", new IsAdminRoute(guild));
                get("/adminInfos", new AdminInfos(guild, databaseConnection));
            });
        });
    }
}
