package fr.gravendev.multibot.tasks;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.database.dao.InfractionDAO;
import fr.gravendev.multibot.database.data.InfractionData;
import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

import java.sql.SQLException;
import java.util.List;
import java.util.TimerTask;

public class InfractionsTask extends TimerTask {

    private final Guild guild;
    private final InfractionDAO infractionDAO;
    private final GuildIdDAO guildIdDAO;

    public InfractionsTask(JDA jda, DatabaseConnection databaseConnection) {
        this.guild = jda.getGuildById(new GuildIdDAO(databaseConnection).get("guild").id);
        this.infractionDAO = new InfractionDAO(databaseConnection);
        this.guildIdDAO = new GuildIdDAO(databaseConnection);
    }

    @Override
    public void run() {
        try {
            List<InfractionData> allUnfinished = infractionDAO.getALLUnfinished();
            allUnfinished.forEach(infraction -> {
                switch (infraction.getType()) {
                    case BAN:
                        guild.getController().unban(infraction.getPunished_id()).queue(success -> {
                        }, throwable -> {
                        });
                        break;
                    case MUTE:
                        Member member = guild.getMemberById(infraction.getPunished_id());
                        if (member == null) break;
                        GuildUtils.removeRole(member, guildIdDAO.get("muted").id + "").queue();
                        break;
                }
                infraction.setFinished(true);
                infractionDAO.save(infraction);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
