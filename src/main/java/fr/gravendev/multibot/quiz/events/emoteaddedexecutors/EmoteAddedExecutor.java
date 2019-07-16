package fr.gravendev.multibot.quiz;

import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

public interface EmoteAddedExecutor {

    String getSaloon();

    void execute(MessageReactionAddEvent event);

}
