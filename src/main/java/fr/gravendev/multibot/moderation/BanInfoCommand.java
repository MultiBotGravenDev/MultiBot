package fr.gravendev.multibot.moderation;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BanInfoCommand implements CommandExecutor {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy à HH:mm:ss");
    private static final Pattern mentionUserPattern = Pattern.compile("<@!?([0-9]{8,})>");
    private final DatabaseConnection databaseConnection;

    public BanInfoCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
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
    public void execute(Message message, String[] args) {
        if (args.length == 0 || extractId(args[0]) == null) {
            message.getChannel().sendMessage(Utils.buildEmbed(Color.RED,"Utilisation: baninfo @utilisateur")).queue();
            return;
        }

        String id = extractId(args[0]);
        message.getJDA().retrieveUserById(id).queue(user -> {
            Guild guild = message.getGuild();

            guild.getBanList().queue((banList) -> {

                MessageEmbed embed;

                if (banList.stream().anyMatch(ban -> ban.getUser().getId().equals(user.getId()))) {

                    InfractionDAO infractionDAO = new InfractionDAO(databaseConnection);
                    InfractionData data = infractionDAO.get(id);
                    if (data != null) {

                        Date dateEnd = data.getEnd();
                        String end = dateEnd == null ? "Jamais" : dateFormat.format(dateEnd);

                        embed = new EmbedBuilder().setColor(Color.RED)
                                .setTitle("Informations de ban " + user.getName())
                                .addField("Raison:", data.getReason(), false)
                                .addField("Date de fin:", end, false)
                                .addField("Par:", "<@" + data.getPunisher_id() + ">", false)
                                .addField("Le:", dateFormat.format(data.getStart()), false).build();

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

    public static String extractId(String id) {
        Matcher matcher = mentionUserPattern.matcher(id);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

}
