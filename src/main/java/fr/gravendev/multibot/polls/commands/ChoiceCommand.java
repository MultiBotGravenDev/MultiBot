package fr.gravendev.multibot.polls.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.polls.PollsManager;
import net.dv8tion.jda.api.entities.Message;

public class ChoiceCommand implements CommandExecutor {

    private final PollsManager pollsManager;

    ChoiceCommand(PollsManager pollsManager) {
        this.pollsManager = pollsManager;
    }

    @Override
    public String getCommand() {
        return "choice";
    }

    @Override
    public String getDescription() {
        return "Permet de définir un choix";
    }

    @Override
    public void execute(Message message, String[] args) {

        if (this.pollsManager.hasNotPoll(message.getAuthor())) return;

        if (args.length == 0 || !args[0].matches("[0-9]+") && !args[0].equals("0")) {
            message.getChannel().sendMessage("Erreur. !poll choice <numéro du choix> <texte ou vide>").queue();
            return;
        }

        this.pollsManager.setChoice(message.getAuthor(), args);

    }

}
