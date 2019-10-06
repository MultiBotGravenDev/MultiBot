package fr.gravendev.multibot.spark.routes;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.ExperienceDAO;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.moderation.InfractionType;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

public class IndexInfos implements Route {

    private final Guild guild;
    private final DatabaseConnection databaseConnection;

    public IndexInfos(Guild guild, DatabaseConnection databaseConnection) {
        this.guild = guild;
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String id = request.queryParams("id");

        if (id == null) {
            return "{}";
        }

        Member member = guild.getMemberById(id);

        if (member == null) {
            return "{}";
        }

        return new JSONObject() {{
            String joinDate = member.getTimeJoined().format(Utils.getDateTimeFormatter());
            int userLevel = new ExperienceDAO(databaseConnection).get(id).getLevels();
            List<InfractionData> allInfractions = new InfractionDAO(databaseConnection).getAll(id);
            // TODO In terms of performance, it's better to do one loop and increment each counter
            long infractions = allInfractions.stream()
                    .filter(infraction -> infraction.getType() == InfractionType.WARN).count();
            long bans = allInfractions.stream()
                    .filter(infraction -> infraction.getType() == InfractionType.BAN).count();
            long mutes = allInfractions.stream()
                    .filter(infraction -> infraction.getType() == InfractionType.MUTE).count();
            int memberCount = guild.getMembers().size();
            String guildCreationDate = guild.getTimeCreated().format(Utils.getDateTimeFormatter());

            put("joinDate", joinDate);
            put("userLevel", userLevel);
            put("infractions", infractions);
            put("bans", bans);
            put("mutes", mutes);
            put("members", memberCount);
            put("age", guildCreationDate);
        }};
    }
}
