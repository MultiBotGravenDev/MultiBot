package fr.gravendev.multibot.events.listeners;

import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.quizz.QuizManager;
import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.impl.RoleImpl;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

public class EmoteAddedListener implements Listener<MessageReactionAddEvent> {

    private final QuizManager quizManager;

    public EmoteAddedListener(QuizManager quizManager) {
        this.quizManager = quizManager;
    }

    @Override
    public Class<MessageReactionAddEvent> getEventClass() {
        return MessageReactionAddEvent.class;
    }

    @Override
    public void executeListener(MessageReactionAddEvent event) {

        Member member = event.getMember();

        if (!event.getChannel().getName().equalsIgnoreCase("lisez-ce-salon")) return;
        if (!event.getReactionEmote().getName().equalsIgnoreCase("\u2705")) return;
        if (GuildUtils.hasRole(member, "membre")) return;

        this.quizManager.createQuiz(member.getUser());
    }

}
