package fr.gravendev.multibot.commands.commands;

import net.dv8tion.jda.api.entities.Message;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GoogleCommand implements CommandExecutor {
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

        if (args.length == 0) {
            return;
        }

        String encodedSearch = Arrays.stream(args)
                .map(URLEncoder::encode)
                .collect(Collectors.joining("+"));
        message.getChannel().sendMessage("https://www.google.com/search?q=" + encodedSearch).queue();

    }

}
