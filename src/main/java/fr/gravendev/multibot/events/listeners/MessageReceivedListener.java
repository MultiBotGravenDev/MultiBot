package fr.gravendev.multibot.events.listeners;

import fr.gravendev.multibot.commands.CommandManager;
import fr.gravendev.multibot.data.ExperienceData;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.ExperienceDAO;
import fr.gravendev.multibot.events.Listener;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class MessageReceivedListener implements Listener<MessageReceivedEvent> {

    private final CommandManager commandManager;
    private final DatabaseConnection databaseConnection;

    public MessageReceivedListener(CommandManager commandManager, DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        this.commandManager = commandManager;
    }

    @Override
    public Class<MessageReceivedEvent> getEventClass() {
        return MessageReceivedEvent.class;
    }

    @Override
    public void executeListener(MessageReceivedEvent event) {

        if (commandManager.executeCommand(event.getMessage())) return;

        try {
            Connection connection = databaseConnection.getConnection();
            User author = event.getAuthor();

            int xpEarned = ThreadLocalRandom.current().nextInt(15, 26);

            ExperienceDAO experienceDAO = new ExperienceDAO(connection);
            ExperienceData experienceData = experienceDAO.get(author.getId());

            if(experienceData != null) {
                if(experienceData.getLastMessage().getTime() + 60000 < System.currentTimeMillis()) {
                    experienceData.addMessage();
                    experienceData.addExperience(xpEarned);

                    int requireToLevelUp = levelToExp(experienceData.getLevels());
                    if(experienceData.getExperiences() > requireToLevelUp) {
                        experienceData.addLevel();
                        experienceData.removeExperience(requireToLevelUp);
                    }

                    experienceDAO.save(experienceData);
                }
            } else {
                experienceData = new ExperienceData(author.getId());
                experienceDAO.save(experienceData);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private int levelToExp(int level) {
        return (5 * level) * 2 + (50 * level + 100);
    }

}
