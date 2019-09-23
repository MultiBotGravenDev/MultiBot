package fr.gravendev.multibot.tasks.antiroles;

import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.utils.Configuration;

public class AntiRepost extends AntiRole {

    public AntiRepost(DAOManager daoManager) {
        super(daoManager, Configuration.ANTI_REPOST);
    }

}
