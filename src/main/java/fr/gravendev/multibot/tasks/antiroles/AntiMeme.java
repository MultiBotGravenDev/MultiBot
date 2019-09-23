package fr.gravendev.multibot.tasks.antiroles;

import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.utils.Configuration;

public class AntiMeme extends AntiRole {

    public AntiMeme(DAOManager daoManager) {
        super(daoManager, Configuration.ANTI_MEME);
    }

}
