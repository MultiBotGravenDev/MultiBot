package fr.gravendev.multibot.spark.routes;

import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.moderation.InfractionType;
import fr.gravendev.multibot.utils.Configuration;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import java.util.ArrayList;
import java.util.List;

public class MainRoute implements Route {

    private final Guild guild;
    private final InfractionDAO infractionDAO;

    public MainRoute(Guild guild, DAOManager daoManager) {
        this.guild = guild;
        this.infractionDAO = daoManager.getInfractionDAO();
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Session session = request.session();
        String user = session.attribute("user");
        if (user == null) {
            response.status(403);
            return response.status();
        }
        JSONObject jsonUser = new JSONObject(user);

        Member guildMember = guild.getMemberById(jsonUser.getString("id"));
        boolean isAdmin = guildMember != null && guildMember.hasPermission(Permission.ADMINISTRATOR);

        JSONObject count = new JSONObject();
        int membres = countMembersWithRole(Configuration.MEMBER.getValue());
        long nonMembres = guild.getMembers().stream().filter(member -> !member.getUser().isBot() && member.getRoles().size() == 0).count();
        count.put("nonMembres", nonMembres);
        count.put("membres", membres);
        count.put("honorables", countMembersWithRole(Configuration.HONORABLE.getValue()));
        count.put("developpeurs", countMembersWithRole(Configuration.DEVELOPPEUR.getValue()));
        count.put("piliers", countMembersWithRole(Configuration.PILIER.getValue()));

        List<InfractionData> allInfractions = infractionDAO.getALL(jsonUser.getString("id"));
        JSONObject member = new JSONObject();
        member.put("bans", countSanctionWithType(allInfractions, InfractionType.BAN));
        member.put("mutes", countSanctionWithType(allInfractions, InfractionType.MUTE));
        member.put("infractions", countSanctionWithType(allInfractions, InfractionType.WARN));

        JSONObject discord = new JSONObject();
        discord.put("creation", guild.getTimeCreated().format(Utils.getDateTimeFormatter()));
        String polls = Configuration.SONDAGES.getValue();
        JSONObject lastPoll = new JSONObject();

        if(isAdmin) {
            for (Message message : getHistory(polls)) {
                if(!message.getAuthor().isBot() || message.getReactions().size() == 0 || message.getEmbeds().size() != 1) continue;
                MessageEmbed messageEmbed = message.getEmbeds().get(0);

                lastPoll.put("author", messageEmbed.getFooter().getText());
                lastPoll.put("title", messageEmbed.getTitle());

                List<String> fields = new ArrayList<>();
                for (String field : messageEmbed.getDescription().split("\n")) {
                    if(field.length() == 0) continue;
                    fields.add(field);
                }
                lastPoll.put("fields", fields);
                break;
            }
            discord.put("lastPoll", lastPoll);
        }

        JSONObject data = new JSONObject();
        data.put("count", count);
        data.put("member", member);
        data.put("discord", discord);

        return data;
    }

    private int countMembersWithRole(String roleId) {
        Role role = guild.getRoleById(roleId);
        return guild.getMembersWithRoles(role).size();
    }

    private long countSanctionWithType(List<InfractionData> infractions, InfractionType infractionType) {
        return infractions.stream().filter(infraction -> infraction.getType() == infractionType).count();
    }

    private List<Message> getHistory(String channelID) {
        TextChannel pollsChannel = guild.getTextChannelById(channelID);

        MessageHistory messageHistory = pollsChannel.getHistoryAround(pollsChannel.getLatestMessageId(), 10).complete();
        return messageHistory.getRetrievedHistory();
    }

}
