package fr.gravendev.multibot.events.listeners;

import fr.gravendev.multibot.events.Listener;
import fr.gravendev.multibot.quizz.QuizManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.impl.RoleImpl;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

public class EmoteAddedListener implements Listener<MessageReactionAddEvent> {

    private static final long MEMBER_ROLE_ID = 380337877843836929L;

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

        Guild guild = event.getGuild();
        Member member = event.getMember();

        if (!event.getChannel().getName().equalsIgnoreCase("lisez-ce-salon")) return;
        if (guild.getMembersWithRoles(new RoleImpl(MEMBER_ROLE_ID, guild)).contains(member)) return;

        this.quizManager.createQuiz(member.getUser());

    }

}
