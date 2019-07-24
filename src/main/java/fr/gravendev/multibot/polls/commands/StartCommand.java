package fr.gravendev.multibot.polls.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.polls.PollsManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

public class StartCommand implements CommandExecutor {
    private final PollsManager pollsManager;

    StartCommand(PollsManager pollsManager) {
        this.pollsManager = pollsManager;
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

        message.getAuthor().openPrivateChannel().queue(privateChannel ->
                new MessageBuilder()
                        .setContent("!help pour voir la liste des commandes disponible")
                        .setEmbed(new EmbedBuilder()
                                .setTitle(" ")
                                .setFooter(message.getAuthor().getName(), message.getAuthor().getAvatarUrl())
                                .build())
                        .sendTo(privateChannel)
                        .queue(message1 -> this.pollsManager.registerPoll(message.getAuthor(), message1.getIdLong())));
    }

}
