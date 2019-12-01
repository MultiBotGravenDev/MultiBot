package fr.gravendev.multibot.spark.routes.auth;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

public class UserRoute implements Route {

    private Guild guild;

    public UserRoute(Guild guild) {
        this.guild = guild;
    }

    @Override
    public Object handle(Request request, Response response) {
        Session session = request.session();
        String jsonUser = session.attribute("user");
        if (jsonUser == null) {
            response.status(403);
            return response.status();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user", new JSONObject(jsonUser));
        Member member = guild.getMemberById(jsonObject.getJSONObject("user").getString("id"));
        jsonObject.put("admin", member != null && member.hasPermission(Permission.ADMINISTRATOR));

        return jsonObject;
    }
}
