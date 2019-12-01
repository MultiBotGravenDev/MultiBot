package fr.gravendev.multibot.spark.routes.auth;

import bell.oauth.discord.domain.User;
import bell.oauth.discord.main.OAuthBuilder;
import fr.gravendev.multibot.utils.Configuration;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

public class CallbackRoute implements Route {

    @Override
    public Object handle(Request request, Response response) {
        Session session = request.session();
        OAuthBuilder oAuthBuilder = session.attribute("oauth");
        String url = Configuration.URL.getValue();
        if (oAuthBuilder == null) {
            response.redirect(url);
            return null;
        }

        String code = request.queryParams("code");
        bell.oauth.discord.main.Response exchange = oAuthBuilder.exchange(code);
        if (exchange == bell.oauth.discord.main.Response.ERROR) {
            response.redirect(url);
            return null;
        }

        JSONObject jsonUser = new JSONObject();
        User user = oAuthBuilder.getUser();
        jsonUser.put("id", user.getId());
        jsonUser.put("name", user.getUsername());
        jsonUser.put("discriminator", user.getDiscriminator());
        session.attribute("user", jsonUser.toString());

        response.redirect(url);
        return null;
    }
}
