package fr.gravendev.multibot.quiz.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.utils.Configuration;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Arrays;
import java.util.List;

public class ChannelCommand implements CommandExecutor {

    @Override
    public String getCommand() {
        return "channel";
    }

    @Override
    public String getDescription() {
        return "Permet de changer le channel où sont envoyées les candidatures.";
    }

    @Override
    public List<String> getAuthorizedChannelsNames() {
        return Arrays.asList("lisez-ce-salon", "piliers");
    }

    @Override
    public void execute(Message message, String[] args) {

        List<TextChannel> mentionedChannels = message.getMentionedChannels();
        if (mentionedChannels.size() != 1) {
            message.getChannel().sendMessage("Erreur. "+getCharacter()+"quiz channel #channel").queue();
            return;
        }

        TextChannel textChannel = mentionedChannels.get(0);
        Configuration.CANDIDS.setValue(textChannel.getId());
        message.getChannel().sendMessage("Le nouveau salon pour envoyer les candidatures a bien été définis à : " + textChannel.getAsMention()).queue();

    }

}
