package fr.gravendev.multibot.roles.commands;

import fr.gravendev.multibot.commands.commands.CommandCategory;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.RoleChannelsDAO;
import fr.gravendev.multibot.database.data.RoleChannelData;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class ChannelCommand implements CommandExecutor {

    private final RoleChannelsDAO rolesChannelsDAO;

    public ChannelCommand(DAOManager daoManager) {
        this.rolesChannelsDAO = daoManager.getRoleChannelsDAO();
    }

    @Override
    public String getCommand() {
        return "channels";
    }

    @Override
    public String getDescription() {
        return "Associe un channel à un role pour les mentions";
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

        MessageChannel channel = message.getChannel();

        if (args.length == 0) {
            channel.sendMessage("Erreur. &channels <set|remove>").queue();
            return;
        }

        if (!args[0].equalsIgnoreCase("set") && !args[0].equalsIgnoreCase("remove")) {
            channel.sendMessage("Erreur. &channels <set|remove>").queue();
            return;
        }

        if (args[0].equalsIgnoreCase("set")) {
            add(message, args);
            return;
        }

        remove(message, args);

    }

    private void add(Message message, String[] args) {

        MessageChannel channel = message.getChannel();

        if (args.length < 3) {
            channel.sendMessage("Erreur. &channels add <id du channel> <id du rôle").queue();
            return;
        }

        if (message.getGuild().getGuildChannelById(args[1]) == null) {
            channel.sendMessage("Ce channel n'existe pas").queue();
            return;
        }

        if (message.getGuild().getRoleById(args[2]) == null) {
            channel.sendMessage("Ce rôle n'existe pas").queue();
            return;
        }

        this.rolesChannelsDAO.save(new RoleChannelData(args[2], args[1]));
        channel.sendMessage("Ajout effectué").queue();
    }

    private void remove(Message message, String[] args) {

        MessageChannel channel = message.getChannel();

        if (args.length < 2) {
            channel.sendMessage("Erreur. &channels add <id du channel>").queue();
            return;
        }

        this.rolesChannelsDAO.delete(new RoleChannelData("", args[1]));
        channel.sendMessage("Suppression effectuée").queue();
    }

}
