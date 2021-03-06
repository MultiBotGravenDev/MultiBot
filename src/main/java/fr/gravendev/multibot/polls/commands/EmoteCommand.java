package fr.gravendev.multibot.polls.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.polls.PollsManager;
import fr.gravendev.multibot.utils.EmojiUtils;
import net.dv8tion.jda.api.entities.Message;

public class EmoteCommand implements CommandExecutor {

    private final PollsManager pollsManager;

    EmoteCommand(PollsManager pollsManager) {
        this.pollsManager = pollsManager;
    }

    @Override
    public String getCommand() {
        return "emote";
    }

    @Override
    public String getDescription() {
        return "Permet de définir l'emote d'un choix";
    }

    @Override
    public void execute(Message message, String[] args) {

        if (this.pollsManager.hasNotPoll(message.getAuthor())) return;

        if (args.length != 2 || !args[0].matches("[0-9]+") || !EmojiUtils.containsEmoji(args[1])) {
            message.getChannel().sendMessage("Erreur. "+getCharacter()+"poll emote <numéro du choix> <:emote:>").queue();
            return;
        }

        this.pollsManager.setEmote(message.getAuthor(), Integer.parseInt(args[0]), args[1]);

    }

}
