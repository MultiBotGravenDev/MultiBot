package fr.gravendev.multibot.moderation.commands;

import fr.gravendev.multibot.commands.commands.CommandCategory;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.moderation.InfractionType;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnbanCommand implements CommandExecutor {

    private static final Pattern mentionUserPattern = Pattern.compile("<@!?([0-9]{8,})>");
    private final InfractionDAO infractionDAO;

    public UnbanCommand(DAOManager daoManager) {
        this.infractionDAO = daoManager.getInfractionDAO();
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
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    @Override
    public boolean isAuthorizedMember(Member member) {
        return member.hasPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(Message message, String[] args) {

        if (args.length == 0 || extractId(args[0]) == null) {
            message.getChannel().sendMessage(Utils.errorArguments(getCommand(), "@utilisateur")).queue();
            return;
        }

        // TODO: Replace this by UserSearchUtils, when it is possible to get the user id without retrieving it.
        String id = extractId(args[0]);

        message.getGuild().retrieveBanList().queue((banList) -> {
            if (banList.stream().noneMatch(ban -> ban.getUser().getId().equals(id))) {
                message.getChannel().sendMessage(Utils.buildEmbed(Color.RED, "Cet utilisateur n'est pas banni !")).queue();
                return;
            }

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

            message.getGuild().unban(id).queue();

            User user = message.getJDA().getUserCache().getElementById(id);

            message.getChannel().sendMessage(Utils.buildEmbed(Color.DARK_GRAY, (user != null ? user.getName() : id) + " vient d'être unban")).queue();

            if(user == null) {
                return;
            }

            user.openPrivateChannel().queue(privateChannel ->  privateChannel.sendMessage("Vous avez été débanni du discord GravenCommunity !").queue());
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