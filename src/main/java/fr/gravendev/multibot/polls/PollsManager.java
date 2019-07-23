package fr.gravendev.multibot.polls;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PollsManager {

    private final GuildIdDAO guildIdDAO;
    private Map<Long, Poll> polls = new HashMap<>();

    public PollsManager(DatabaseConnection databaseConnection) {
        this.guildIdDAO = new GuildIdDAO(databaseConnection);
    }

    public void registerPoll(User user, long messageId) {
        this.polls.put(user.getIdLong(), new Poll(user, messageId));
    }

    public void setColor(User user, Colors color) {
        if (!this.polls.containsKey(user.getIdLong())) return;
        this.polls.get(user.getIdLong()).setColor(color);
    }

    public void setTitle(User user, String title) {
        if (!this.polls.containsKey(user.getIdLong())) return;
        this.polls.get(user.getIdLong()).setTitle(title);
    }

    public void setChoice(User user, String[] choice) {
        if (!this.polls.containsKey(user.getIdLong())) return;
        this.polls.get(user.getIdLong()).setChoice(Integer.parseInt(choice[0]), String.join(" ", Arrays.copyOfRange(choice, 1, choice.length)));
    }

    public void setEmote(User user, int numberEmote, String emote) {
        if (!this.polls.containsKey(user.getIdLong())) return;
        this.polls.get(user.getIdLong()).setEmote(numberEmote, emote);
    }

    public void finish(User user) {
        if (!this.polls.containsKey(user.getIdLong())) return;
        long guildId = this.guildIdDAO.get("guild").id;
        long sondagesVerifId = this.guildIdDAO.get("sondages_verif").id;

        TextChannel channel = user.getJDA().getGuildById(guildId).getTextChannelById(sondagesVerifId);
        this.polls.get(user.getIdLong()).finish(channel, false);
    }

    public void sendFinalPoll(User user, String title) {
        long userId = user.getIdLong();
        long guildId = this.guildIdDAO.get("guild").id;
        long sondagesId = this.guildIdDAO.get("sondages").id;
        if (!this.polls.containsKey(userId) || !this.polls.get(userId).isSameTitle(title)) return;

        TextChannel channel = user.getJDA().getGuildById(guildId).getTextChannelById(sondagesId);
        this.polls.get(userId).finish(channel, true);
    }

    public void removePoll(User user) {
        this.polls.remove(user.getIdLong());
    }

}
