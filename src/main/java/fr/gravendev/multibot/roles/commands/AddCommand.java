package fr.gravendev.multibot.roles.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.dao.RoleDAO;
import fr.gravendev.multibot.database.data.RoleData;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

import java.util.Arrays;
import java.util.List;

public class AddCommand implements CommandExecutor {

    private final RoleDAO roleDAO;

    AddCommand(DAOManager daoManager) {
        this.roleDAO = daoManager.getRoleDAO();
    }

    @Override
    public String getCommand() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "Permet d'ajouter un rôle.";
    }

    @Override
    public List<String> getAuthorizedChannelsNames() {
        return Arrays.asList("rôle-langage", "piliers");
    }

    @Override
    public void execute(Message message, String[] args) {

        List<Role> mentionedRoles = message.getMentionedRoles();
        boolean isIncorrectCommand = args.length != 3 || !args[0].matches("[0-9]+") || !args[1].matches("[0-9]+");
        if (isIncorrectCommand) {
            message.getChannel().sendMessage("Erreur. " + getCharacter() + "roles add <id de l'emote> <id du channel> <nom du role>").queue();
            return;
        }

        List<Role> rolesByName = message.getGuild().getRolesByName(args[2], true);
        boolean roleNotExist = rolesByName.size() != 0 && roleDAO.get(rolesByName.get(0).getId()) == null;
        if (roleNotExist) {

            saveRole(message, args[0], args[1], args);
            return;
        }

        message.getChannel().sendMessage("Ce role existe déjà").queue();

    }

    private void saveRole(Message message, String emoteId, String channelId, String[] args) {
        Role mentionedRole = message.getGuild().getRolesByName(args[2], true).get(0);
        Emote emote = message.getGuild().getEmoteById(emoteId);

        if (emote == null) {
            message.getChannel().sendMessage("Cette emote n'existe pas").queue();
            return;
        }

        roleDAO.save(new RoleData(mentionedRole.getId(), emoteId, channelId));

        message.getChannel().sendMessage("Le role "
                + mentionedRole.getAsMention()
                + " a bien été ajouté à la liste des rôles avec la réaction "
                + emote.getAsMention()).queue();
    }

}
