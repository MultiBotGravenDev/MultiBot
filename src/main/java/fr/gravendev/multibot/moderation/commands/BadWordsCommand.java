package fr.gravendev.multibot.moderation.commands;

import fr.gravendev.multibot.commands.commands.CommandCategory;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.BadWordsDAO;
import fr.gravendev.multibot.database.data.BadWordsData;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.Arrays;

public class BadWordsCommand implements CommandExecutor {

    private final BadWordsDAO badWordsDAO;

    public BadWordsCommand(DatabaseConnection databaseConnection) {
        this.badWordsDAO = new BadWordsDAO(databaseConnection);
    }

    @Override
    public String getCommand() {
        return "badwords";
    }

    @Override
    public String getDescription() {
        return "Commandes relatives aux badWords. [add - remove - list]";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    @Override
    public boolean isAuthorizedMember(Member member) {
        return member.hasPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(Message message, String[] args) {

        if (args.length == 0) return;

        switch (args[0]) {

            case "add":
                add(message, args);
                break;

            case "remove":
                remove(message, args);
                break;

            case "list":
                list(message.getChannel());
                break;

        }

    }

    private void add(Message message, String[] args) {

        if (args.length < 2) return;

        this.badWordsDAO.save(new BadWordsData(this.badWordsDAO.get("").getBadWords() + args[1]));
        message.getChannel().sendMessage("Le mot " + args[1] + " a bien été ajouté aux bad words.").queue();

    }

    private void remove(Message message, String[] args) {

        if (args.length < 2) return;

        this.badWordsDAO.delete(new BadWordsData(args[1]));
        message.getChannel().sendMessage("Le mot " + args[1] + " a bien été retiré des bad words.").queue();

    }

    private void list(MessageChannel channel) {
        channel.sendMessage(Arrays.toString(this.badWordsDAO.get("").getBadWords().split(" "))).queue();
    }

}
