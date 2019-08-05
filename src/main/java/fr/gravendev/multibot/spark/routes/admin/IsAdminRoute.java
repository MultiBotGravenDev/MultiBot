package fr.gravendev.multibot.spark.routes.admin;

import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import spark.Request;
import spark.Response;
import spark.Route;

public class IsAdminRoute implements Route {

    private final Guild guild;
    public IsAdminRoute(Guild guild) {
        this.guild = guild;
    }

    @Override
    public Object handle(Request request, Response response) {
        String id = request.queryParams("id");
        if (id == null) {
            return false;
        }

        Member member = guild.getMemberById(id);
        if(member == null) {
            return false;
        }

        return member.hasPermission(Permission.ADMINISTRATOR);
    }
}
