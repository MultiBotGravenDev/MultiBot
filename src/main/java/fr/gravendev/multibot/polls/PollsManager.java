package fr.gravendev.multibot.polls;

import fr.gravendev.multibot.utils.Configuration;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PollsManager {
    private Map<Long, Poll> polls = new HashMap<>();

    public void registerPoll(User user, long messageId) {
        this.polls.put(user.getIdLong(), new Poll(user, messageId));
    }

    public void removePoll(User user) {
        this.polls.remove(user.getIdLong());
    }

    public void setColor(User user, Colors color) {
        getUserPoll(user).setColor(color);
    }

    public void setTitle(User user, String title) {
        getUserPoll(user).setTitle(title);
    }

    private Poll getUserPoll(long userId) {
        return polls.get(userId);
    }

    private Poll getUserPoll(User user) {
        return getUserPoll(user.getIdLong());
    }

    public void setChoice(User user, String[] choice) {
        getUserPoll(user).setChoice(Integer.parseInt(choice[0]), String.join(" ", Arrays.copyOfRange(choice, 1, choice.length)));
    }

    public void setEmote(User user, int numberEmote, String emote) {
        getUserPoll(user).setEmote(numberEmote, emote);
    }

    public void sendToValidation(User user) {
        String guildId = Configuration.GUILD.getValue();
        String sondagesVerifId = Configuration.SONDAGES_VERIF.getValue();

        TextChannel channel = user.getJDA().getGuildById(guildId).getTextChannelById(sondagesVerifId);
        getUserPoll(user).finish(channel, false);
    }

    public void send(User user, String title) {
        long userId = user.getIdLong();
        String guildId = Configuration.GUILD.getValue();
        String sondagesId = Configuration.SONDAGES.getValue();
        if (hasNotPoll(userId)
                || !getUserPoll(userId).isSameTitle(title)) {
            return;
        }

        TextChannel channel = user.getJDA().getGuildById(guildId).getTextChannelById(sondagesId);
        getUserPoll(userId).finish(channel, true);
    }

    // TODO Revert condition : hasPoll instead of hasNotPoll
    public boolean hasNotPoll(User user) {
        return hasNotPoll(user.getIdLong());
    }

    private boolean hasNotPoll(long userId) {
        return !polls.containsKey(userId);
    }
}
