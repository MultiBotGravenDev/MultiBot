package fr.gravendev.multibot.quiz.events;

import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.quiz.QuizManager;
import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;

public class EmoteRemovedListener implements Listener<MessageReactionRemoveEvent> {

    private final QuizManager quizManager;

    public EmoteRemovedListener(QuizManager quizManager) {
        this.quizManager = quizManager;
    }

    @Override
    public Class<MessageReactionRemoveEvent> getEventClass() {
        return MessageReactionRemoveEvent.class;
    }

    @Override
    public void executeListener(MessageReactionRemoveEvent event) {

        if (event.getUser().isBot()) return;

        Member member = event.getMember();

        if (!event.getChannel().getName().equalsIgnoreCase("lisez-ce-salon")) return;
        if (!event.getReactionEmote().getName().equalsIgnoreCase("\u2705")) return;
        if (GuildUtils.hasRole(member, "member")) return;

        this.quizManager.removeQuiz(member.getUser());
    }

}
