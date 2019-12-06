package fr.gravendev.multibot.roles.commands;

import fr.gravendev.multibot.commands.commands.CommandCategory;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.RoleDAO;
import fr.gravendev.multibot.database.data.RoleData;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;

import java.util.HashMap;
import java.util.Map;

public class PingCommand implements CommandExecutor {

    private final RoleDAO roleDAO;

    private final Map<Long, Long> coolDowns;

    public PingCommand(DAOManager daoManager) {
        this.roleDAO = daoManager.getRoleDAO();
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

        RoleData roleData = this.roleDAO.get(message.getChannel().getId());

        if (roleData == null) {
            channel.sendMessage("La commande n'est pas disponible dans ce salon").queue();
            return;
        }

        Role role = message.getGuild().getRoleById(roleData.getRoleId());

        if (role == null) {
            channel.sendMessage("Ce rôle n'existe pas").queue();
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
