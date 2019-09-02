package fr.gravendev.multibot.roles.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
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

    ListCommand(DatabaseConnection databaseConnection) {
        this.roleDAO = new RoleDAO(databaseConnection);
    }

    @Override
    public String getCommand() {
        return "list";
    }

    @Override
    public List<String> getAuthorizedChannelsNames() {
        return Arrays.asList("rôle-langage-test", "piliers");
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
                    Role role = guild.getRoleById(roleData.roleId);
                    String roleName = role == null ? "INVALID(" + roleData.roleId + ")" : role.getAsMention();

                    Emote emote = guild.getEmoteById(roleData.emoteId);
                    String emoteName = emote == null ? "INVALID(" + roleData.emoteId + ")" : emote.getAsMention();

                    return roleName + " (" + emoteName + ")";
                })
                .collect(Collectors.joining(" - "));

        message.getChannel().sendMessage("Listes des rôles enregistrés : " + roles).queue();
    }

}
