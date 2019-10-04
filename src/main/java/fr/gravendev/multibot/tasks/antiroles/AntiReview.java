package fr.gravendev.multibot.tasks.antiroles;

import fr.gravendev.multibot.database.dao.AntiRolesDAO;
import fr.gravendev.multibot.utils.Configuration;
import net.dv8tion.jda.api.entities.Guild;

public class AntiReview extends AntiRole {

    public AntiReview(Guild guild, AntiRolesDAO antiRolesDAO) {
        super(guild, antiRolesDAO, Configuration.ANTI_REVIEW);
    }

}
