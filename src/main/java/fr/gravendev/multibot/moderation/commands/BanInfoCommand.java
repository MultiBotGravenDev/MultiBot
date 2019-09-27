package fr.gravendev.multibot.moderation.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandCategory;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.moderation.InfractionType;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BanInfoCommand implements CommandExecutor {

    private static final Pattern mentionUserPattern = Pattern.compile("<@!?([0-9]{8,})>");
    private final InfractionDAO infractionDAO;

    public BanInfoCommand(DAOManager daoManager) {
        this.infractionDAO = daoManager.getInfractionDAO();
    }

    @Override
    public String getCommand() {
        return "baninfo";
    }

    @Override
    public String getDescription() {
        return "Obtenir des informations sur le bannissement d'un membre.";
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.GUILD;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    @Override
    public void execute(Message message, String[] args) {
        if (args.length == 0 || extractId(args[0]) == null) {
            message.getChannel().sendMessage(Utils.errorArguments(getCommand(), "@utilisateur")).queue();
            return;
        }

        String id = extractId(args[0]);

        message.getJDA().retrieveUserById(id).queue(user -> {
            Guild guild = message.getGuild();

            guild.retrieveBanList().queue((banList) -> {

                MessageEmbed embed;

                if (banList.stream().anyMatch(ban -> ban.getUser().getId().equals(user.getId()))) {

                    InfractionData data;
                    try {
                        data = infractionDAO.getLast(id, InfractionType.BAN);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return;
                    }

                    if (data != null) {

                        Date dateEnd = data.getEnd();
                        String end = dateEnd == null ? "Jamais" : Utils.getDateFormat().format(dateEnd);

                        embed = new EmbedBuilder().setColor(Color.RED)
                                .setTitle("Informations de ban " + user.getName())
                                .addField("Raison:", data.getReason(), false)
                                .addField("Date de fin:", end, false)
                                .addField("Par:", "<@" + data.getPunisherId() + ">", false)
                                .addField("Le:", Utils.getDateFormat().format(data.getStart()), false).build();

                    } else
                        embed = Utils.buildEmbed(Color.RED, "Impossible de trouver les informations de bannissement");
                } else
                    embed = Utils.buildEmbed(Color.RED, "Cet utilisateur n'est pas bannis !");

                message.getChannel().sendMessage(embed).queue();
            });
        });
    }

    @Override
    public boolean isAuthorizedMember(Member member) {
        return member.hasPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public boolean isAuthorizedChannel(MessageChannel channel) {
        return true;
    }

    private static String extractId(String id) {
        Matcher matcher = mentionUserPattern.matcher(id);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

}
