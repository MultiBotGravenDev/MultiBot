package fr.gravendev.multibot.polls.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.polls.PollsManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

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

        if (!this.pollsManager.hasNotPoll(message.getAuthor())) {
            message.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Annulez la création du sondage avant d'en créer un autre.").queue());
            return;
        }

        message.getAuthor().openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage(new EmbedBuilder()
                        .setTitle(" ")
                        .setFooter(message.getAuthor().getName(), message.getAuthor().getAvatarUrl())
                        .build())
                        .queue(message1 -> this.pollsManager.registerPoll(message.getAuthor(), message1.getIdLong())));
    }

}
