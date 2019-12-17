package fr.gravendev.multibot.roles.commands;

import fr.gravendev.multibot.commands.commands.CommandCategory;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.RoleChannelsDAO;
import fr.gravendev.multibot.database.data.RoleChannelData;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;

import java.util.HashMap;
import java.util.Map;

public class AideCommand implements CommandExecutor {

    private final RoleChannelsDAO roleChannelsDAO;

    private final Map<Long, Long> coolDowns;

    public AideCommand(DAOManager daoManager) {
        this.roleChannelsDAO = daoManager.getRoleChannelsDAO();
        this.coolDowns = new HashMap<>();
    }

    @Override
    public String getCommand() {
        return "aide";
    }

    @Override
    public String getDescription() {
        return "permet de ping un rôle";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.SYSTEM;
    }

    @Override
    public void execute(Message message, String[] args) {

        MessageChannel channel = message.getChannel();

        RoleChannelData roleData = this.roleChannelsDAO.get(message.getChannel().getId());

        if (roleData == null) {
            channel.sendMessage("La commande n'est pas disponible dans ce salon").queue();
            return;
        }

        Role role = message.getGuild().getRoleById(roleData.roleId);

        if (role == null) {
            channel.sendMessage("Erreur").queue();
            return;
        }

        if (!this.coolDowns.containsKey(role.getIdLong())) {
            this.coolDowns.put(role.getIdLong(), System.currentTimeMillis() - 3 * 60 * 60 * 1000);
        }

        if (System.currentTimeMillis() - this.coolDowns.get(role.getIdLong()) < 2 * 60 * 60 * 1000) {
            message.getChannel().sendMessage("Une mention a déjà été faite il y a moins de 2h, désolé").queue();
            return;
        }

        role.getManager().setMentionable(true).queue();
        message.getChannel().sendMessage("Demande d'aide de " + message.getAuthor().getAsMention() + " pour le langage " + role.getAsMention()).queue();
        role.getManager().setMentionable(false).queue();
        this.coolDowns.replace(role.getIdLong(), System.currentTimeMillis());

    }

}
