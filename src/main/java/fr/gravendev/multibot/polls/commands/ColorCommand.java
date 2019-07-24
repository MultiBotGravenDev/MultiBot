package fr.gravendev.multibot.polls.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.polls.Colors;
import fr.gravendev.multibot.polls.PollsManager;
import net.dv8tion.jda.core.entities.Message;

public class ColorCommand implements CommandExecutor {

    private PollsManager pollsManager;

    public ColorCommand(PollsManager pollsManager) {
        this.pollsManager = pollsManager;
    }

    @Override
    public String getCommand() {
        return "color";
    }

    @Override
    public String getDescription() {
        return "Permet de changer la couleur du sondage";
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.PRIVATE;
    }

    @Override
    public void execute(Message message, String[] args) {
        if (args.length == 0 || this.pollsManager.hasNotPoll(message.getAuthor())) return;
        this.pollsManager.setColor(message.getAuthor(), Colors.fromString(args[0]));
    }

}
