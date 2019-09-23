package fr.gravendev.multibot.quiz.events.emoteaddedexecutors;


import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public interface EmoteAddedExecutor {

    String getSaloonId();

    void execute(MessageReactionAddEvent event);

}
