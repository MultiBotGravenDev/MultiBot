package fr.gravendev.multibot.commands;

import net.dv8tion.jda.core.entities.Message;

public interface CommandExecutor {

    String getCommand();

    String getDescription();

    ChannelType getChannelType();

    void execute(Message message, String[] args);

}
