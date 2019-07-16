package fr.gravendev.multibot.events.listeners;

import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

public interface EmoteAddedExecutor {

    String getSaloon();

    void execute(MessageReactionAddEvent event);

}
