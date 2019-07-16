package fr.gravendev.multibot.commands.commands;

import fr.gravendev.multibot.commands.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;

import java.util.List;

public  interface CommandExecutor {

    String getCommand();

    String getDescription();

    ChannelType getChannelType();

    void execute(Message message, String[] args);

    List<String> getAuthorizedChannelsNames();

    boolean isAuthorizedMember(Member member);

    default boolean isAuthorizedChannel(MessageChannel channel) {
        if (getChannelType() != ChannelType.PRIVATE) {
            return getAuthorizedChannelsNames().contains(channel.getName()) || channel instanceof PrivateChannel;
        } else {
            return getChannelType().isEqualsTo(channel.getType());
        }
    }

}
