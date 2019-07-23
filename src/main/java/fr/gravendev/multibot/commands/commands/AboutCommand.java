package fr.gravendev.multibot.commands.commands;

import fr.gravendev.multibot.commands.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.util.Collections;
import java.util.List;

public class AboutCommand implements CommandExecutor {

    @Override
    public String getCommand() {
        return "about";
    }

    @Override
    public String getDescription() {
        return "Commande à propos ¯\\_(ツ)_/¯";
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.ALL;
    }

    @Override
    public List<String> getAuthorizedChannelsNames() {
        return Collections.singletonList("commandes");
    }

    @Override
    public boolean isAuthorizedMember(Member member) {
        return true;
    }

    @Override
    public void execute(Message message, String[] args) {

        message.getChannel().sendMessage("Le MultiBot a été développé par les piliers de la commu.").queue();

    }

}
