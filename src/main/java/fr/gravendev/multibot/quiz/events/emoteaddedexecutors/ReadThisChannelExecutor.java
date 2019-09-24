package fr.gravendev.multibot.quiz.events.emoteaddedexecutors;

import fr.gravendev.multibot.quiz.QuizManager;
import fr.gravendev.multibot.utils.Configuration;
import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class ReadThisChannelExecutor implements EmoteAddedExecutor {


    private final QuizManager quizManager;

    public ReadThisChannelExecutor(QuizManager quizManager) {
        this.quizManager = quizManager;
    }

    @Override
    public String getChannelId() {
        return Configuration.READ_THIS_SALOON.getValue();
    }

    @Override
    public void execute(MessageReactionAddEvent event) {

        Member member = event.getMember();
        MessageReaction.ReactionEmote reactionEmote = event.getReactionEmote();

        if (!reactionEmote.getName().equalsIgnoreCase("\u2705") && member != null) {
            event.getReaction().removeReaction(member.getUser()).queue();
            return;
        }

        if (GuildUtils.hasRole(member, "Membre")) return;

        this.quizManager.createQuiz(member.getUser());

    }

}
