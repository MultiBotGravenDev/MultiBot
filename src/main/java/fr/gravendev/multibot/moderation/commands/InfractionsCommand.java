package fr.gravendev.multibot.moderation.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandCategory;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.awt.*;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

public class InfractionsCommand implements CommandExecutor {

    private final InfractionDAO infractionDAO;

    public InfractionsCommand(DAOManager daoManager) {
        this.infractionDAO = daoManager.getInfractionDAO();
    }

    @Override
    public String getCommand() {
        return "infractions";
    }

    @Override
    public String getDescription() {
        return "Afficher les avertissements d'un membre.";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.GUILD;
    }

    @Override
    public void execute(Message message, String[] args) {
        try {

            List<Member> mentionedMembers = message.getMentionedMembers();
            if (mentionedMembers.size() == 0) {
                message.getChannel().sendMessage(Utils.buildEmbed(Color.RED, "Utilisation: infractions @membre")).queue();
                return;
            }

            Member member = mentionedMembers.get(0);

            List<InfractionData> allInfractions = infractionDAO.getALLInfractions(member.getUser().getId());

            Date lastDayDate = Date.from(Instant.now().minusSeconds(60 * 60 * 24));
            Date lastWeekDate = Date.from(Instant.now().minusSeconds(60 * 60 * 24 * 7));

            long lastWeek = allInfractions.stream().filter(infraction -> infraction.getStart().after(lastWeekDate)).count();
            long lastDay = allInfractions.stream().filter(infraction -> infraction.getStart().after(lastDayDate)).count();
            int total = allInfractions.size();

            EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED)
                    .setAuthor("Infractions de " + member.getUser().getAsTag(), member.getUser().getAvatarUrl())
                    .addField("24 dernières heures", lastDay + " infraction" + (lastDay > 1 ? "s" : ""), true)
                    .addField("7 derniers jours", lastWeek + " infraction" + (lastWeek > 1 ? "s" : ""), true)
                    .addField("Total", total + " infraction" + (total > 1 ? "s" : ""), true);

            if (allInfractions.size() == 0) {
                embedBuilder.addField("10 dernières infractions", "Aucune", false);
            } else {

                StringBuilder builder = new StringBuilder();

                allInfractions.stream().limit(10).forEach(infraction -> builder.append("**")
                        .append(infraction.getReason())
                        .append("** - ")
                        .append(Utils.getDateFormat().format(infraction.getStart()))
                        .append("\n"));
                embedBuilder.addField("10 dernières infractions", builder.toString(), false);
            }

            message.getChannel().sendMessage(embedBuilder.build()).queue();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isAuthorizedMember(Member member) {
        return member.hasPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public boolean isAuthorizedChannel(MessageChannel channel) {
        return true;
    }

}
