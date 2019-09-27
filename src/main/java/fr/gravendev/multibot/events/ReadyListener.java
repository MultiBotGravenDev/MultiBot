package fr.gravendev.multibot.events;

import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.tasks.AntiRolesTask;
import fr.gravendev.multibot.tasks.InfractionsTask;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.ReadyEvent;

import java.util.Timer;

public class ReadyListener implements Listener<ReadyEvent> {

    private final DAOManager daoManager;

    ReadyListener(DAOManager daoManager) {
        this.daoManager = daoManager;
    }

    @Override
    public Class<ReadyEvent> getEventClass() {
        return ReadyEvent.class;
    }

    @Override
    public void executeListener(ReadyEvent event) {
        JDA jda = event.getJDA();
        new Thread(() -> new Timer().schedule(new AntiRolesTask(jda, daoManager), 0, 10_000)).start();
        new Thread(() ->  new Timer().schedule(new InfractionsTask(jda, daoManager), 0, 10_000)).start();
    }

}
