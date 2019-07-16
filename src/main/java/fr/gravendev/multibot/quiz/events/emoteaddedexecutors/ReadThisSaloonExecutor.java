package fr.gravendev.multibot.quiz.emoteaddedexecutors;

import fr.gravendev.multibot.quiz.EmoteAddedExecutor;
import fr.gravendev.multibot.quiz.QuizManager;
import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

public class ReadThisSaloonExecutor implements EmoteAddedExecutor {


    private final QuizManager quizManager;

    public ReadThisSaloonExecutor(QuizManager quizManager) {
        this.quizManager = quizManager;
    }

    @Override
    public String getSaloon() {
        return "lisez-ce-salon";
    }

    @Override
    public void execute(MessageReactionAddEvent event) {

        Member member = event.getMember();

        if (!event.getReactionEmote().getName().equalsIgnoreCase("\u2705")) return;
        if (GuildUtils.hasRole(member, "membre")) return;

        this.quizManager.createQuiz(member.getUser());

    }

}
