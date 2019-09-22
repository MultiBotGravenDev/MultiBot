package fr.gravendev.multibot.tasks.antiroles;

import fr.gravendev.multibot.database.dao.DAOManager;

public class AntiReview extends AntiRole {

    public AntiReview(DAOManager daoManager) {
        super(daoManager, "anti-review");
    }

}
