package fr.gravendev.multibot.spark.routes;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.ExperienceDAO;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.data.ExperienceData;
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
        if(member == null) {
            return "{}";
        }

        JSONObject object = new JSONObject();

        ExperienceDAO experienceDAO = new ExperienceDAO(databaseConnection);
        ExperienceData experienceData = experienceDAO.get(id);

        InfractionDAO infractionDAO = new InfractionDAO(databaseConnection);
        List<InfractionData> allInfractions = infractionDAO.getAll(id);

        long infractions = allInfractions.stream().filter(infraction -> infraction.getType() == InfractionType.WARN).count();
        long bans = allInfractions.stream().filter(infraction -> infraction.getType() == InfractionType.BAN).count();
        long mutes = allInfractions.stream().filter(infraction -> infraction.getType() == InfractionType.MUTE).count();

        object.put("joinDate", member.getTimeJoined().format(Utils.getDateTimeFormatter()));
        object.put("userLevel", experienceData.getLevels());
        object.put("infractions", infractions);
        object.put("bans", bans);
        object.put("mutes", mutes);

        object.put("members", guild.getMembers().size());
        object.put("age", guild.getTimeCreated().format(Utils.getDateTimeFormatter()));

        return object;
    }
}
