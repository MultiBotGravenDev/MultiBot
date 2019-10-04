package fr.gravendev.multibot.roles.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.RoleDAO;
import fr.gravendev.multibot.database.data.RoleData;
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
                .map(Role::getId)
                .map(roleDAO::get)
                .filter(Objects::nonNull)
                .map(roleData -> getRoleDescription(guild, roleData))
                .collect(Collectors.joining(" - "));

        message.getChannel().sendMessage("Listes des rôles enregistrés : " + roles).queue();
    }


    private String getRoleDescription(Guild guild, RoleData roleData) {
        String roleName = getRoleName(guild, roleData);

        String emoteName = getRoleEmote(guild, roleData);

        return roleName + " (" + emoteName + ")";
    }


    private String getRoleName(Guild guild, RoleData roleData) {
        String roleId = roleData.getRoleId();
        Role role = guild.getRoleById(roleId);

        if (role != null) {
            return role.getName();
        }

        return "ERROR(" + roleId + ")";
    }


    private String getRoleEmote(Guild guild, RoleData roleData) {
        String emoteId = roleData.getEmoteId();
        Emote emote = guild.getEmoteById(emoteId);

        if (emote != null) {
            return emote.getAsMention();
        }

        return "ERROR(" + emoteId + ")";
    }


}
