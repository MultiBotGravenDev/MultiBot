package fr.gravendev.multibot.moderation.commands;

import fr.gravendev.multibot.commands.commands.CommandCategory;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.BadWordsDAO;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.data.BadWordsData;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.Arrays;

public class BadWordsCommand implements CommandExecutor {

    private final BadWordsDAO badWordsDAO;

    public BadWordsCommand(DAOManager daoManager) {
        this.badWordsDAO = daoManager.getBadWordsDAO();
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

        if (args.length == 0){
            message.getChannel().sendMessage(Utils.errorArguments(getCommand(), "<add,remove,list> [word]")).queue();
            return;
        }

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

        if (args.length < 2){
            message.getChannel().sendMessage(Utils.errorArguments(getCommand(), "add <word>")).queue();
            return;
        }

        this.badWordsDAO.save(new BadWordsData(this.badWordsDAO.get("").getBadWords() + args[1]));
        message.getChannel().sendMessage("Le mot " + args[1] + " a bien été ajouté aux bad words.").queue();

    }

    private void remove(Message message, String[] args) {

        if (args.length < 2){
            message.getChannel().sendMessage(Utils.errorArguments(getCommand(), "remove <word>")).queue();
            return;
        }

        this.badWordsDAO.delete(new BadWordsData(args[1]));
        message.getChannel().sendMessage("Le mot " + args[1] + " a bien été retiré des bad words.").queue();

    }

    private void list(MessageChannel channel) {
        channel.sendMessage("Liste des mots interdits: "+ Arrays.toString(this.badWordsDAO.get("").getBadWords().split(" "))).queue();
    }

}
