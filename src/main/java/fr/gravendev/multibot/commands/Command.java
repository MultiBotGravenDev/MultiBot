package fr.gravendev.multibot.commands;

import net.dv8tion.jda.core.entities.Message;

public interface Command {

    String getCommand();

    boolean execute(Message message);

}
