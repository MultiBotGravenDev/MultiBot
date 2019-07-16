package fr.gravendev.multibot.commands;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;

import java.util.List;

public abstract class CommandExecutor {

    public abstract String getCommand();

    public abstract String getDescription();

    public abstract ChannelType getChannelType();

    public abstract void execute(Message message, String[] args);

    public abstract List<String> getAuthorizedChannelsNames();

    public abstract boolean isAuthorizedMember(Member member);

    boolean isAuthorizedChannel(MessageChannel channel) {
        if (getChannelType() != ChannelType.PRIVATE) {
            return getAuthorizedChannelsNames().contains(channel.getName()) || channel instanceof PrivateChannel;
        } else {
            return getChannelType().isEqualsTo(channel.getType());
        }
    }

}
