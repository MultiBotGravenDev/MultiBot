package fr.gravendev.multibot.tasks;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.tasks.antiroles.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.util.*;

public class AntiRolesTask extends TimerTask {

    private final Guild guild;
    private final List<AntiRole> antiRoles;

    public AntiRolesTask(JDA jda, DatabaseConnection databaseConnection) {
        this.guild = jda.getGuildById(new GuildIdDAO(databaseConnection).get("guild").id);

        this.antiRoles = Arrays.asList(
                new AntiRepost(databaseConnection),
                new AntiMeme(databaseConnection),
                new AntiReview(databaseConnection),
                new AntiImage(databaseConnection)
        );
    }

    @Override
    public void run() {
        antiRoles.forEach(antiRole -> antiRole.deleteRoles(guild));
    }

}
