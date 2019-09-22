package fr.gravendev.multibot.tasks.antiroles;

import fr.gravendev.multibot.database.dao.DAOManager;

public class AntiImage extends AntiRole {

    public AntiImage(DAOManager daoManager) {
        super(daoManager, "anti-image");
    }

}
