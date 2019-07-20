package fr.gravendev.multibot.tasks;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.moderation.commands.antiroles.AntiMeme;
import fr.gravendev.multibot.moderation.commands.antiroles.AntiRepost;
import fr.gravendev.multibot.moderation.commands.antiroles.AntiReview;
import fr.gravendev.multibot.moderation.commands.antiroles.AntiRole;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;

import java.sql.SQLException;
import java.util.*;

public class AntiRolesTask extends TimerTask {

    private final Guild guild;
    private final List<AntiRole> antiRoles;

    public AntiRolesTask(JDA jda, DatabaseConnection databaseConnection) {
        Guild guild1;
        try {
            guild1 = jda.getGuildById(new GuildIdDAO(databaseConnection).get("guild").id);
        } catch (SQLException e) {
            e.printStackTrace();
            guild1 = null;
        }
        this.guild = guild1;

        this.antiRoles = Arrays.asList(
                new AntiRepost(databaseConnection),
                new AntiMeme(databaseConnection),
                new AntiReview(databaseConnection)
        );
    }

    @Override
    public void run() {

        this.antiRoles.forEach(antiRole -> antiRole.deleteRoles(this.guild));

    }

}
