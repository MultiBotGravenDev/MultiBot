package fr.gravendev.multibot.polls.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.polls.PollsManager;
import net.dv8tion.jda.api.entities.Message;

public class AskCommand implements CommandExecutor {

    private final PollsManager pollsManager;

    AskCommand(PollsManager pollsManager) {
        this.pollsManager = pollsManager;
    }

    @Override
    public String getCommand() {
        return "ask";
    }

    @Override
    public String getDescription() {
        return "Permet de définir la question d'un sondage";
    }

    @Override
    public void execute(Message message, String[] args) {

        if (this.pollsManager.hasNotPoll(message.getAuthor())) return;
        this.pollsManager.setTitle(message.getAuthor(), String.join(" ", args));

    }

}
