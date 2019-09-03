package fr.gravendev.multibot.moderation.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandCategory;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.ImmunisedIdDAO;
import fr.gravendev.multibot.database.data.ImmunisedIdsData;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.Collections;
import java.util.stream.Collectors;

public class ImmuniseCommand implements CommandExecutor {

    private final ImmunisedIdDAO immunisedIdDAO;

    public ImmuniseCommand(DatabaseConnection databaseConnection) {
        this.immunisedIdDAO = new ImmunisedIdDAO(databaseConnection);
    }

    @Override
    public String getCommand() {
        return "immunise";
    }

    @Override
    public String getDescription() {
        return "Commandes relatives aux immunités des rôles pour les warn. [add - remove - list]";
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.GUILD;
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
                if (message.getGuild().getRoleById(args[1]) == null) return;
                add(message, args);
                break;

            case "remove":
                if (message.getGuild().getRoleById(args[1]) == null) return;
                remove(message, args);
                break;

            case "list":
                list(message);
                break;

        }

    }

    private void add(Message message, String[] args) {

        ImmunisedIdsData immunisedIdsData = this.immunisedIdDAO.get("");
        immunisedIdsData.immunisedIds.add(Long.valueOf(args[1]));

        this.immunisedIdDAO.save(new ImmunisedIdsData(immunisedIdsData.immunisedIds));
        message.getChannel().sendMessage("Le role " + message.getGuild().getRoleById(args[1]).getName() + " a bien été ajouté aux rôles immunisés.").queue();

    }

    private void remove(Message message, String[] args) {

        this.immunisedIdDAO.delete(new ImmunisedIdsData(Collections.singletonList(Long.valueOf(args[1]))));
        message.getChannel().sendMessage("Le role " + message.getGuild().getRoleById(args[1]).getName() + " a bien été retiré des rôles immunisés.").queue();

    }

    private void list(Message message) {
        String roles = this.immunisedIdDAO.get("").immunisedIds.stream()
                .map(id -> message.getGuild().getRoleById(id).getName())
                .collect(Collectors.joining(" - "));
        message.getChannel().sendMessage("[" + roles + "]").queue();
    }

}
