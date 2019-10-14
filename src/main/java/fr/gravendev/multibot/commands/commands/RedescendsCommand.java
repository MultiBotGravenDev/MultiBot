package fr.gravendev.multibot.commands.commands;

import fr.gravendev.multibot.utils.TextFormatter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.w3c.dom.Text;

public class RedescendsCommand implements CommandExecutor {

    @Override
    public String getCommand() {
        return "redescends";
    }

    @Override
    public String getDescription() {
        return "Envoie un message gentil";
    }

    @Override
    public boolean isAuthorizedMember(Member member) {
        return member.hasPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    @Override
    public void execute(Message message, String[] args) {
        String formattedText = TextFormatter.formatLines(
                "Il semblerait que tu surestimes tes compétences et que tu dénigres les autres : tu sembles souffrir d'un effet de Dunning-Kruger.",
                "",
                "Nous t'invitons donc à découvrir ce dont il s'agit : https://fr.wikipedia.org/wiki/Effet_Dunning-Kruger et à corriger cela, pour toi et pour les autres.").toString();

        message.getChannel().sendMessage(formattedText).queue();
    }

}
