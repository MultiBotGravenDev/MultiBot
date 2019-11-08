package fr.gravendev.multibot.quiz.events.emoteaddedexecutors;

import fr.gravendev.multibot.quiz.MemberQuestionsManager;
import fr.gravendev.multibot.quiz.QuizManager;
import fr.gravendev.multibot.utils.Configuration;
import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.Random;

public class ReadThisChannelExecutor implements EmoteAddedExecutor {

    private final String[][] questions = new String[][]{
            {"Combien font 5 + 4 ?", "9"},
            {"Quelle est la première lettre du nom du serveur ?", "G"},
            {"Quelle est la première lettre du mot \"développeur\" ?", "d"},
            {"Combien font 10 / 2 ?", "5"},
            {"De quelle couleur est le haut du G de Graven ?", "orange"},
            {"Combien de lettres compose le pseudo \"Graven\" ?", "6"},
            {"Combien font 5 - 4 ?", "1"},

    };

    private final QuizManager quizManager;
    private final MemberQuestionsManager questionsManager;

    public ReadThisChannelExecutor(QuizManager quizManager, MemberQuestionsManager questionsManager) {
        this.quizManager = quizManager;
        this.questionsManager = questionsManager;
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

        int randomNumber = new Random().nextInt(questions.length);

        member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Répondez correctement à cette question pour obtenir le grade membre :\n``" + questions[randomNumber][0] + "``").queue());
        this.questionsManager.addMember(member, questions[randomNumber][1]);

//        this.quizManager.createQuiz(member.getUser());

    }

}
