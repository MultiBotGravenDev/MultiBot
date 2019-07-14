package fr.gravendev.multibot.commands;

import net.dv8tion.jda.core.entities.Message;

public interface CommandExecutor {

    String getCommand();

    void execute(Message message);

}
