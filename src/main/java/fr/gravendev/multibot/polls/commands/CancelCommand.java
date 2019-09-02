package fr.gravendev.multibot.polls.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.polls.PollsManager;
import net.dv8tion.jda.api.entities.Message;

public class CancelCommand implements CommandExecutor {

    private final PollsManager pollsManager;

    CancelCommand(PollsManager pollsManager) {
        this.pollsManager = pollsManager;
    }

    @Override
    public String getCommand() {
        return "cancel";
    }

    @Override
    public String getDescription() {
        return "Permet d'annuler la création d'un sondage";
    }

    @Override
    public void execute(Message message, String[] args) {
        this.pollsManager.removePoll(message.getAuthor());
        message.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Sondage annulé").queue());
    }

}
