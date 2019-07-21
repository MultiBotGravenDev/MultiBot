package fr.gravendev.multibot.commands.commands;

import fr.gravendev.multibot.commands.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;

import java.util.ArrayList;
import java.util.List;

public interface CommandExecutor {

    String getCommand();

    default String getDescription() {
        return "";
    }

    void execute(Message message, String[] args);

    default ChannelType getChannelType() {
        return ChannelType.ALL;
    }

    default List<String> getAuthorizedChannelsNames() {
        return new ArrayList<>();
    }

    default boolean isAuthorizedMember(Member member) {
        return true;
    }

    default boolean isAuthorizedChannel(MessageChannel channel) {
        switch (getChannelType()) {

            case ALL:
                if (channel.getType() == net.dv8tion.jda.core.entities.ChannelType.PRIVATE) return true;

            case GUILD:
                if (getAuthorizedChannelsNames().contains(channel.getName())) return true;
                return getAuthorizedChannelsNames().isEmpty();

            case PRIVATE:
                return channel.getType() == net.dv8tion.jda.core.entities.ChannelType.PRIVATE;
        }
        return false;
    }

    default boolean canExecute(Message message) {
        return isAuthorizedChannel(message.getChannel()) && isAuthorizedMember(message.getMember());
    }

}
