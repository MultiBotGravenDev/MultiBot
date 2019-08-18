package fr.gravendev.multibot.events;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.tasks.AntiRolesTask;
import fr.gravendev.multibot.tasks.InfractionsTask;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.ReadyEvent;

import java.util.Timer;

public class ReadyListener implements Listener<ReadyEvent> {

    private final DatabaseConnection databaseConnection;

    ReadyListener(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Class<ReadyEvent> getEventClass() {
        return ReadyEvent.class;
    }

    @Override
    public void executeListener(ReadyEvent event) {
        JDA jda = event.getJDA();
        new Thread(() -> new Timer().schedule(new AntiRolesTask(jda, databaseConnection), 0, 10_000)).start();
        new Thread(() ->  new Timer().schedule(new InfractionsTask(jda, databaseConnection), 0, 10_000)).start();
    }

}
