package fr.gravendev.multibot.moderation.commands;

import fr.gravendev.multibot.commands.commands.CommandCategory;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.moderation.InfractionType;
import fr.gravendev.multibot.utils.UserSearchUtils;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;
import java.util.OptionalLong;

public class UnbanCommand implements CommandExecutor {

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
        if (args.length == 0) {
            message.getChannel().sendMessage(Utils.errorArguments(getCommand(), "@utilisateur")).queue();
            return;
        }

        OptionalLong opUserId = UserSearchUtils.searchId(args[0]);

        if (!opUserId.isPresent()) {
            UserSearchUtils.sendUserNotFound(message.getChannel());
            return;
        }

        long id = opUserId.getAsLong();

        message.getGuild().retrieveBanList().queue(banList -> {
            Optional<Guild.Ban> opBan = banList.stream()
                    .filter(ban -> ban.getUser().getIdLong() == id)
                    .findAny();

            if (!opBan.isPresent()) {
                message.getChannel().sendMessage(Utils.buildEmbed(Color.RED, "Cet utilisateur n'est pas banni !")).queue();
                return;
            }

            User user = opBan.get().getUser();
            
            InfractionData data;
            try {
                data = infractionDAO.getLast(user.getId(), InfractionType.BAN);
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }

            if (data != null) {
                data.setEnd(new Date());
                data.setFinished(true);
                infractionDAO.save(data);
            }

            message.getGuild().unban(user).queue();
            message.getChannel().sendMessage(Utils.buildEmbed(Color.DARK_GRAY, user.getName() + " vient d'être unban")).queue();

            user.openPrivateChannel().queue(privateChannel ->  privateChannel.sendMessage("Vous avez été débanni du discord GravenCommunity !").queue());
        });
    }

}