package fr.gravendev.multibot.spark.routes.auth;

import bell.oauth.discord.main.OAuthBuilder;
import fr.gravendev.multibot.utils.Configuration;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginRoute implements Route {

    @Override
    public Object handle(Request request, Response response) {

        OAuthBuilder oAuthBuilder = new OAuthBuilder(Configuration.CLIENT_ID.getValue(), Configuration.CLIENT_SECRET.getValue())
                .setScopes(new String[]{"identify"})
                .setRedirectURI(Configuration.REDIRECT_URL.getValue());

        request.session(true).attribute("oauth", oAuthBuilder);
        response.redirect(oAuthBuilder.getAuthorizationUrl(null));
        return null;
    }
}
