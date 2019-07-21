package fr.gravendev.multibot.rank;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.ExperienceDAO;
import fr.gravendev.multibot.database.data.ExperienceData;
import fr.gravendev.multibot.events.Listener;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.ThreadLocalRandom;

public class MessageReceivedListener implements Listener<MessageReceivedEvent> {

    private final DatabaseConnection databaseConnection;

    public MessageReceivedListener(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Class<MessageReceivedEvent> getEventClass() {
        return MessageReceivedEvent.class;
    }

    @Override
    public void executeListener(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;

        User author = event.getAuthor();

        int xpEarned = ThreadLocalRandom.current().nextInt(15, 26);

        ExperienceDAO experienceDAO = new ExperienceDAO(databaseConnection);
        ExperienceData experienceData = experienceDAO.get(author.getId());

        if (experienceData != null) {
            if (experienceData.getLastMessage().getTime() + 60000 < System.currentTimeMillis()) {
                experienceData.addMessage();
                experienceData.addExperience(xpEarned);

                int requireToLevelUp = levelToExp(experienceData.getLevels());
                if (experienceData.getExperiences() > requireToLevelUp) {
                    experienceData.addLevel();
                    experienceData.removeExperience(requireToLevelUp);
                }

                experienceDAO.save(experienceData);
            }
        } else {
            experienceData = new ExperienceData(author.getId());
            experienceDAO.save(experienceData);
        }

    }


    private int levelToExp(int level) {
        return (5 * level) * 2 + (50 * level + 100);
    }

}
