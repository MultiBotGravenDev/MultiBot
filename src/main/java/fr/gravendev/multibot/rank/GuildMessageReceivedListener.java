package fr.gravendev.multibot.rank;

import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.ExperienceDAO;
import fr.gravendev.multibot.database.data.ExperienceData;
import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.utils.Configuration;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.concurrent.ThreadLocalRandom;

public class GuildMessageReceivedListener implements Listener<GuildMessageReceivedEvent> {

    private final ExperienceDAO experienceDAO;

    public GuildMessageReceivedListener(DAOManager daoManager) {
        this.experienceDAO = daoManager.getExperienceDAO();
    }

    @Override
    public Class<GuildMessageReceivedEvent> getEventClass() {
        return GuildMessageReceivedEvent.class;
    }

    @Override
    public void executeListener(GuildMessageReceivedEvent event) {

        if (event.getAuthor().isBot() || !event.getMessage().getGuild().getId().equals(Configuration.GUILD.getValue())) return;

        User author = event.getAuthor();

        int xpEarned = ThreadLocalRandom.current().nextInt(15, 26);

        ExperienceData experienceData = experienceDAO.get(author.getId());
        if (experienceData != null) {
            if (experienceData.getLastMessage().getTime() + 60000 < System.currentTimeMillis()) {
                experienceData.incrementMessageCount();
                experienceData.addExperiencePoints(xpEarned);

                int requireToLevelUp = levelToExp(experienceData.getLevels());
                if (experienceData.getExperiences() > requireToLevelUp) {
                    experienceData.incrementLevel();
                    experienceData.removeExperiencePoints(requireToLevelUp);
                }

                experienceDAO.save(experienceData);
            }
        } else {
            experienceData = new ExperienceData(author.getId());
            experienceDAO.save(experienceData);
        }
    }

    private int levelToExp(int level) {
        return 5 * level * level + 50 * level + 100;
    }

}
