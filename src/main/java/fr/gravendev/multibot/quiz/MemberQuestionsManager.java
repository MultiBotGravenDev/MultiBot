package fr.gravendev.multibot.quiz;

import fr.gravendev.multibot.utils.Configuration;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.Map;

public class MemberQuestionsManager {

    private final Map<Long, String> questions = new HashMap<>();

    public void addMember(Member member, String response) {
        this.questions.put(member.getIdLong(), response);
    }

    public void removeMmeber(Member member) {
        this.questions.remove(member.getIdLong());
    }

    public boolean isWaitingFor(User author) {
        return this.questions.containsKey(author.getIdLong());
    }

    public void registerResponse(User author, String contentDisplay) {

        if (contentDisplay.equalsIgnoreCase(this.questions.get(author.getIdLong()))) {

            Guild guild = author.getJDA().getGuildById(Configuration.GUILD.getValue());
            guild.addRoleToMember(guild.getMember(author), guild.getRoleById(Configuration.MEMBER.getValue())).queue();
            author.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Bonne réponse, bienvenue sur le serveur !").queue());


        } else {

            author.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Mauvaise réponse, désolé").queue());

        }

        this.questions.remove(author.getIdLong());

    }

}
