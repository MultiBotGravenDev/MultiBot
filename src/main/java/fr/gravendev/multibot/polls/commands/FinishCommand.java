package fr.gravendev.multibot.polls.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.polls.PollsManager;
import net.dv8tion.jda.core.entities.Message;

public class FinishCommand implements CommandExecutor {

    private final PollsManager pollsManager;

    public FinishCommand(PollsManager pollsManager) {
        this.pollsManager = pollsManager;
    }

    @Override
    public String getCommand() {
        return "finish";
    }

    @Override
    public String getDescription() {
        return "Permet de valider un sondage";
    }

    @Override
    public void execute(Message message, String[] args) {

        if (this.pollsManager.hasNotPoll(message.getAuthor())) return;
        this.pollsManager.sendToValidation(message.getAuthor());

    }

}
