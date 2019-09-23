package fr.gravendev.multibot.moderation.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandCategory;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.ImmunisedIdDAO;
import fr.gravendev.multibot.database.data.ImmunisedIdsData;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;
import java.util.Collections;
import java.util.stream.Collectors;

public class ImmuniseCommand implements CommandExecutor {

    private final ImmunisedIdDAO immunisedIdDAO;

    public ImmuniseCommand(DAOManager daoManager) {
        this.immunisedIdDAO = daoManager.getImmunisedIdDAO();
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

        if (args.length == 0) {
            message.getChannel().sendMessage(Utils.errorArguments(getCommand(), "<add, remove, list> [Role ID]")).queue();
            return;
        }

        switch (args[0]) {

            case "list":
                list(message);
                break;

            case "add":
                if (args.length < 2 || message.getGuild().getRoleById(args[1]) == null) {
                    message.getChannel().sendMessage(Utils.buildEmbed(Color.RED, "Erreur: L'ID du rôle est incorrect.")).queue();
                    return;
                }
                add(message, args);
                break;

            case "remove":
                if (args.length < 2 || message.getGuild().getRoleById(args[1]) == null) {
                    message.getChannel().sendMessage(Utils.buildEmbed(Color.RED, "Erreur: L'ID du rôle est incorrect.")).queue();
                    return;
                }
                remove(message, args);
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
        message.getChannel().sendMessage("List des rôles immunisés: [" + roles + "]").queue();
    }

}
