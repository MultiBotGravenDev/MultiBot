package fr.gravendev.multibot.tasks.antiroles;

import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.utils.Configuration;

public class AntiReview extends AntiRole {

    public AntiReview(DAOManager daoManager) {
        super(daoManager, Configuration.ANTI_REVIEW);
    }

}
