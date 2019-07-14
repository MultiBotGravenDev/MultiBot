package fr.gravendev.multibot.commands.commands;

import fr.gravendev.multibot.commands.ChannelCommandExecutor;
import fr.gravendev.multibot.commands.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

public class AboutCommandExecutor implements ChannelCommandExecutor {

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
    public void execute(Message message, String[] args) {

        if (isAuthorizedChannel(message.getChannel()))
        message.getChannel().sendMessage("Le MultiBot a été développé par les piliers de la commu.").queue();
    }

    @Override
    public boolean isAuthorizedChannel(MessageChannel channel) {
        return "_commandes".equalsIgnoreCase(channel.getName());
    }
}
