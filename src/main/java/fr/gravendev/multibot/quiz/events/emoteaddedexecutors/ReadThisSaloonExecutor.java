package fr.gravendev.multibot.quiz.events.emoteaddedexecutors;

import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.GuildIdDAO;
import fr.gravendev.multibot.quiz.QuizManager;
import fr.gravendev.multibot.utils.GuildUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

public class ReadThisSaloonExecutor implements EmoteAddedExecutor {


    private final QuizManager quizManager;
    private final GuildIdDAO guildIdDAO;

    public ReadThisSaloonExecutor(QuizManager quizManager, DatabaseConnection databaseConnection) {
        this.quizManager = quizManager;
        this.guildIdDAO = new GuildIdDAO(databaseConnection);
    }

    @Override
    public long getSaloonId() {
        return this.guildIdDAO.get("read_this_saloon").id;
    }

    @Override
    public void execute(MessageReactionAddEvent event) {

        Member member = event.getMember();
        MessageReaction.ReactionEmote reactionEmote = event.getReactionEmote();

        if (!reactionEmote.getName().equalsIgnoreCase("\u2705")) {
            event.getReaction().removeReaction(member.getUser()).queue();
            return;
        }

        if (GuildUtils.hasRole(member, "membre")) return;

        this.quizManager.createQuiz(member.getUser());

    }

}
