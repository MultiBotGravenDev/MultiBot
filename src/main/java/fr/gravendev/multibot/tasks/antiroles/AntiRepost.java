package fr.gravendev.multibot.tasks.antiroles;

import fr.gravendev.multibot.database.dao.DAOManager;

public class AntiRepost extends AntiRole {

    public AntiRepost(DAOManager daoManager) {
        super(daoManager, "anti-repost");
    }

}
