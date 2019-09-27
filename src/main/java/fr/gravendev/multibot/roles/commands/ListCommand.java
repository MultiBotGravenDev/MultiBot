package fr.gravendev.multibot.roles.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.RoleDAO;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ListCommand implements CommandExecutor {

    private final RoleDAO roleDAO;

    ListCommand(DAOManager daoManager) {
        this.roleDAO = daoManager.getRoleDAO();
    }

    @Override
    public String getCommand() {
        return "list";
    }

    @Override
    public List<String> getAuthorizedChannelsNames() {
        return Arrays.asList("rôle-langage", "piliers", "commandes");
    }

    @Override
    public String getDescription() {
        return "Permet de voir la liste des rôles.";
    }

    @Override
    public void execute(Message message, String[] args) {

        Guild guild = message.getGuild();

        String roles = guild.getRoles().stream()
                .map(role -> roleDAO.get(role.getId()))
                .filter(Objects::nonNull)
                .map(roleData -> {
                    Role role = guild.getRoleById(roleData.getRoleId());
                    String roleName = role == null ? "INVALID(" + roleData.getRoleId() + ")" : role.getName();

                    Emote emote = guild.getEmoteById(roleData.getEmoteId());
                    String emoteName = emote == null ? "INVALID(" + roleData.getEmoteId() + ")" : emote.getAsMention();

                    return roleName + " (" + emoteName + ")";
                })
                .collect(Collectors.joining(" - "));

        message.getChannel().sendMessage("Listes des rôles enregistrés : " + roles).queue();
    }

}
