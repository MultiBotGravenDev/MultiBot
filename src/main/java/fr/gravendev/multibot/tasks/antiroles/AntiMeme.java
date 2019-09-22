package fr.gravendev.multibot.tasks.antiroles;

import fr.gravendev.multibot.database.dao.DAOManager;

public class AntiMeme extends AntiRole {

    public AntiMeme(DAOManager daoManager) {
        super(daoManager, "anti-meme");
    }

}
