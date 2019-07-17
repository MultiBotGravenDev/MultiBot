package fr.gravendev.multibot.roles.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.RoleDAO;
import fr.gravendev.multibot.database.data.RoleData;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class AddCommand implements CommandExecutor {

    private final DatabaseConnection databaseConnection;

    AddCommand(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public String getCommand() {
        return "add";
    }


    @Override
    public List<String> getAuthorizedChannelsNames() {
        return Arrays.asList("rôle-langage", "piliers");
    }

    @Override
    public void execute(Message message, String[] args) {

        List<Role> mentionedRoles = message.getMentionedRoles();

        if (args.length != 2) return;
        if (!args[0].matches("[0-9]+")) return;
        if (mentionedRoles.size() != 1) return;

        try {
            RoleDAO roleDAO = new RoleDAO(this.databaseConnection.getConnection());

            Role mentionedRole = mentionedRoles.get(0);

            if (roleDAO.get(mentionedRole.getId()) == null) {

                Emote emote = message.getGuild().getEmoteById(args[0]);

                if (emote == null) {
                    message.getChannel().sendMessage("Cet emote n'existe pas").queue();
                    return;
                }

                roleDAO.save(new RoleData(mentionedRole.getId(), args[0]));
                message.getChannel().sendMessage("Le role "
                        + mentionedRole.getAsMention()
                        + " a bien été ajouté à la liste des rôles avec la réaction "
                        + emote.getAsMention()).queue();
            } else {
                message.getChannel().sendMessage("Ce role existe déjà").queue();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
