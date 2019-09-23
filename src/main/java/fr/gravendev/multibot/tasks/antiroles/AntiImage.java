package fr.gravendev.multibot.tasks.antiroles;

import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.utils.Configuration;

public class AntiImage extends AntiRole {

    public AntiImage(DAOManager daoManager) {
        super(daoManager, Configuration.ANTI_IMAGE);
    }

}
