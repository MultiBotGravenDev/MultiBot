package fr.gravendev.multibot.commands.commands;

import net.dv8tion.jda.api.entities.Message;

public class GoogleCommand implements CommandExecutor{
    @Override
    public String getCommand() {
        return "google";
    }

    @Override
    public String getDescription() {
        return "envoie un lien vers un site magique";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.UTILS;
    }

    @Override
    public void execute(Message message, String[] args) {

        message.getChannel().sendMessage("https://www.google.com/search?q=" + String.join("+", args)).queue();

    }

}
