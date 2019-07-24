package fr.gravendev.multibot.moderation.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.moderation.InfractionType;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnbanCommand implements CommandExecutor {

    private static final Pattern mentionUserPattern = Pattern.compile("<@!?([0-9]{8,})>");
    private DatabaseConnection databaseConnection;

    public UnbanCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getCommand() {
        return "unban";
    }

    @Override
    public String getDescription() {
        return "Débannir un utilisateur";
    }

    @Override
    public boolean isAuthorizedMember(Member member) {
        return member.hasPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(Message message, String[] args) {
        if (args.length == 0 || extractId(args[0]) == null) {
            message.getChannel().sendMessage(Utils.buildEmbed(Color.RED, "Utilisation: unban @utilisateur")).queue();
            return;
        }

        String id = extractId(args[0]);

        message.getGuild().getBanList().queue((banList) -> {
            if (banList.stream().noneMatch(ban -> ban.getUser().getId().equals(id))) {
                message.getChannel().sendMessage(Utils.buildEmbed(Color.RED, "Cet utilisateur n'est pas bannis")).queue();
                return;
            }

            InfractionDAO infractionDAO = new InfractionDAO(databaseConnection);
            InfractionData data;
            try {
                data = infractionDAO.getLast(id, InfractionType.BAN);
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }

            if (data != null) {
                data.setEnd(new Date());
                data.setFinished(true);
                infractionDAO.save(data);
            }

            message.getGuild().getController().unban(id).queue();
            message.getChannel().sendMessage(Utils.buildEmbed(Color.DARK_GRAY, id + " vient d'être unban")).queue();

        });
    }

    private static String extractId(String id) {
        Matcher matcher = mentionUserPattern.matcher(id);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

}