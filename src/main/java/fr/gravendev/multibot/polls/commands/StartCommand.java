package fr.gravendev.multibot.polls.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.polls.PollsManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class StartCommand implements CommandExecutor {
    private final PollsManager pollsManager;
    private ArrayList<CommandExecutor> commandExecutors;

    StartCommand(PollsManager pollsManager, ArrayList<CommandExecutor> commandExecutors) {
        this.pollsManager = pollsManager;
        this.commandExecutors = commandExecutors;
    }

    @Override
    public String getCommand() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Permet de démarrer la création d'un sondage";
    }

    @Override
    public void execute(Message message, String[] args) {

        if (!this.pollsManager.hasNotPoll(message.getAuthor())) {
            message.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Annulez la création du sondage avant d'en créer un autre.").queue());
            return;
        }

        PrivateChannel privateChannel = message.getAuthor().openPrivateChannel().complete();

        privateChannel.sendMessage(new EmbedBuilder()
                .setTitle(" ")
                .setFooter(message.getAuthor().getName(), message.getAuthor().getAvatarUrl())
                .build())
                .queue(message1 -> this.pollsManager.registerPoll(message.getAuthor(), message1.getIdLong()));

        privateChannel.sendMessage("!poll ["
                + this.commandExecutors.stream().map(CommandExecutor::getCommand).collect(Collectors.joining("/"))
                + "]").queue();

    }

}
