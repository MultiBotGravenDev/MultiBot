package fr.gravendev.multibot.commands.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.utils.Configuration;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.ArrayList;
import java.util.List;

public interface CommandExecutor {

    default char getCharacter() {
        return Configuration.PREFIX.getValue().charAt(0);
    }

    String getCommand();

    String getDescription();

    default CommandCategory getCategory() {
        return CommandCategory.NONE;
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
                if (channel.getType() == net.dv8tion.jda.api.entities.ChannelType.PRIVATE) {
                    return true;
                }

            case GUILD:
                if (getAuthorizedChannelsNames().contains(channel.getName())) {
                    return true;
                }
                return getAuthorizedChannelsNames().isEmpty();

            case PRIVATE:
                return channel.getType() == net.dv8tion.jda.api.entities.ChannelType.PRIVATE;
        }
        return false;
    }

    // TODO: To try!
    default boolean canExecute(Message message) {
        Member member = message.getMember();
        boolean isMemberNotNullAndAdmin = member != null && member.hasPermission(Permission.ADMINISTRATOR);

        // TODO To refactor again : revert condition + remove member != null as long as it has already been tested!
        return isMemberNotNullAndAdmin
                || !(!isAuthorizedChannel(message.getChannel()) || (member != null && !isAuthorizedMember(member)));
    }
}
