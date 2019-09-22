package fr.gravendev.multibot.tasks;

import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.tasks.antiroles.AntiImage;
import fr.gravendev.multibot.tasks.antiroles.AntiMeme;
import fr.gravendev.multibot.tasks.antiroles.AntiRepost;
import fr.gravendev.multibot.tasks.antiroles.AntiReview;
import fr.gravendev.multibot.tasks.antiroles.AntiRole;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;

public class AntiRolesTask extends TimerTask {

    private final Guild guild;
    private final List<AntiRole> antiRoles;

    public AntiRolesTask(JDA jda, DAOManager daoManager) {
        this.guild = jda.getGuildById(daoManager.getGuildIdDAO().get("guild").id);

        this.antiRoles = Arrays.asList(
                new AntiRepost(daoManager),
                new AntiMeme(daoManager),
                new AntiReview(daoManager),
                new AntiImage(daoManager)
        );
    }

    @Override
    public void run() {
        antiRoles.forEach(antiRole -> antiRole.deleteRoles(guild));
    }

}
