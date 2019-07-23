package fr.gravendev.multibot.polls.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.polls.PollsManager;
import net.dv8tion.jda.core.entities.Message;

public class ChoiceCommand implements CommandExecutor {

    private final PollsManager pollsManager;

    public ChoiceCommand(PollsManager pollsManager) {
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

        if (args.length == 0 || !args[0].matches("[0-9]+")) {
            message.getChannel().sendMessage("Erreur. !poll choice <numéro du choix> <texte ou vide>").queue();
            return;
        }

        this.pollsManager.setChoice(message.getAuthor(), args);

    }

}
