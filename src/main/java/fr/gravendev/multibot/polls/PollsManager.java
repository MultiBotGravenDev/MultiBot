package fr.gravendev.multibot.polls;

import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;
import java.util.Map;

public class PollsManager {

    private Map<Long, Poll> polls = new HashMap<>();

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
}
