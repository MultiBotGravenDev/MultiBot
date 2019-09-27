package fr.gravendev.multibot.spark.routes.admin;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.moderation.InfractionType;
import fr.gravendev.multibot.utils.Configuration;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.List;

public class AdminInfos implements Route {
    private Guild guild;
    private DatabaseConnection databaseConnection;

    public AdminInfos(Guild guild, DatabaseConnection databaseConnection) {
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
        if(member == null || !member.hasPermission(Permission.ADMINISTRATOR)) {
            return "{}";
        }

        InfractionDAO infractionDAO = new InfractionDAO(databaseConnection);
        List<InfractionData> allUnfinished = infractionDAO.getALLUnfinished();
        long bans = allUnfinished.stream().filter(infraction -> infraction.getType() == InfractionType.BAN).count();
        long mutes = allUnfinished.stream().filter(infraction -> infraction.getType() == InfractionType.MUTE).count();

        JSONObject lastCandid = new JSONObject();

        String candids = Configuration.CANDIDS.getValue();
        for (Message message : getHistory(candids)) {
            if(!message.getAuthor().isBot() || message.getReactions().size() > 0 || message.getEmbeds().size() != 1) continue;
            MessageEmbed messageEmbed = message.getEmbeds().get(0);

            lastCandid.put("author", messageEmbed.getAuthor().getName());

            List<MessageEmbed.Field> fields = new ArrayList<>();
            for (MessageEmbed.Field field : messageEmbed.getFields()) {
                String value = field.getValue().replaceAll("<[^>]*>","");
                MessageEmbed.Field newField = new MessageEmbed.Field(field.getName(), value, field.isInline());
                fields.add(newField);
            }

            lastCandid.put("fields", fields);
            break;
        }

        String polls = Configuration.SONDAGES.getValue();
        JSONObject lastPoll = new JSONObject();

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

        return new JSONObject().put("bans", bans).put("mutes", mutes).put("candid", lastCandid).put("lastPoll", lastPoll);
    }

    private List<Message> getHistory(String channelID) {
        TextChannel pollsChannel = guild.getTextChannelById(channelID);

        MessageHistory messageHistory = pollsChannel.getHistoryAround(pollsChannel.getLatestMessageId(), 10).complete();
        return messageHistory.getRetrievedHistory();
    }

}
