package fr.gravendev.multibot.commands;

import net.dv8tion.jda.core.entities.MessageChannel;

public interface ChannelCommandExecutor extends CommandExecutor {

    boolean isAuthorizedChannel(MessageChannel channel);

}
