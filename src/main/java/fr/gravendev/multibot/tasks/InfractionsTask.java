package fr.gravendev.multibot.tasks;

import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.moderation.InfractionType;
import fr.gravendev.multibot.utils.Configuration;
import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.sql.SQLException;
import java.util.List;
import java.util.TimerTask;

public class InfractionsTask extends TimerTask {

    private final Guild guild;
    private final InfractionDAO infractionDAO;

    public InfractionsTask(Guild guild, InfractionDAO infractionDAO) {
        this.guild = guild;
        this.infractionDAO = infractionDAO;
    }

    @Override
    public void run() {
        try {

            List<InfractionData> allUnfinished = infractionDAO.getAllFinished();
            allUnfinished.forEach(this::computeFinishedInfraction);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void computeFinishedInfraction(InfractionData infractionData) {
        String punishedId = infractionData.getPunishedId();
        Member member = guild.getMemberById(punishedId);

        executeAction(infractionData, punishedId, member);

        infractionData.setFinished(true);
        infractionDAO.save(infractionData);
    }

    private void executeAction(InfractionData infractionData, String punishedId, Member member) {

        if (infractionData.getType() == InfractionType.MUTE) {
            this.muteAction(member);
            return;
        }

        this.banAction(punishedId, member);
    }

    private void muteAction(Member member) {

        if (member == null) {
            return;
        }

        GuildUtils.removeRole(member, Configuration.MUTED.getValue()).queue();

        String message = "Vous avez été unmute du discord GravenDev";
        member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(message).queue());
    }

    private void banAction(String punishedId, Member member) {
        guild.unban(punishedId).queue();

        if (member == null) {
            return;
        }

        String message = "Vous avez été débanni du discord GravenDev";
        member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(message).queue());
    }

}
