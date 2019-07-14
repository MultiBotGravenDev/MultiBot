package fr.gravendev.multibot.commands.commands;

import fr.gravendev.multibot.commands.ChannelCommandExecutor;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

public class AboutCommandExecutor implements ChannelCommandExecutor {

    @Override
    public String getCommand() {
        return "about";
    }

    @Override
    public void execute(Message message) {

        if (isAuthorizedChannel(message.getChannel()))
        message.getChannel().sendMessage("Le MultiBot a été développé par les pilliers de la commu.").queue();

    }

    @Override
    public boolean isAuthorizedChannel(MessageChannel channel) {
        return "_commandes".equalsIgnoreCase(channel.getName());
    }
}
