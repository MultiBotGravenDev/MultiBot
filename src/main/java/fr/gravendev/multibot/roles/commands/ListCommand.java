package fr.gravendev.multibot.roles.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.RoleDAO;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ListCommand implements CommandExecutor {

    private final DatabaseConnection databaseConnection;

    ListCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getCommand() {
        return "list";
    }

    @Override
    public List<String> getAuthorizedChannelsNames() {
        return Arrays.asList("rôle-langage", "piliers");
    }

    @Override
    public void execute(Message message, String[] args) {

        RoleDAO roleDAO = new RoleDAO(this.databaseConnection);

        Guild guild = message.getGuild();

        String roles = guild.getRoles().stream()
                .map(role -> roleDAO.get(role.getId()))
                .filter(Objects::nonNull)
                .map(roleData -> guild.getRoleById(roleData.roleId).getAsMention() + " (" + guild.getEmoteById(roleData.emoteId).getAsMention() + ")")
                .collect(Collectors.joining(" - "));

        message.getChannel().sendMessage("listes des rôles enregistrés : " + roles).queue();
    }

}
