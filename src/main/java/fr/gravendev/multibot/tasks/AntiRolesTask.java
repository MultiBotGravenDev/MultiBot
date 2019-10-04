package fr.gravendev.multibot.tasks;

import fr.gravendev.multibot.database.dao.AntiRolesDAO;
import fr.gravendev.multibot.tasks.antiroles.AntiImage;
import fr.gravendev.multibot.tasks.antiroles.AntiMeme;
import fr.gravendev.multibot.tasks.antiroles.AntiRepost;
import fr.gravendev.multibot.tasks.antiroles.AntiReview;
import fr.gravendev.multibot.tasks.antiroles.AntiRole;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;

public class AntiRolesTask extends TimerTask {

    private final List<AntiRole> antiRoles;

    public AntiRolesTask(Guild guild, AntiRolesDAO antiRolesDAO) {

        this.antiRoles = Arrays.asList(
                new AntiRepost(guild, antiRolesDAO),
                new AntiMeme(guild, antiRolesDAO),
                new AntiReview(guild, antiRolesDAO),
                new AntiImage(guild, antiRolesDAO)
        );

    }

    @Override
    public void run() {
        this.antiRoles.forEach(AntiRole::deleteRoles);
    }

}
