package fr.gravendev.multibot.commands.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.customs.RemoveCommand;
import fr.gravendev.multibot.commands.commands.customs.SetCommand;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CustomCommand implements CommandExecutor {
    private final List<CommandExecutor> argumentsExecutors;

    public CustomCommand(DAOManager daoManager) {
        this.argumentsExecutors = Arrays.asList(
                new SetCommand(daoManager),
                new RemoveCommand(daoManager)
        );
    }

    @Override
    public String getCommand() {
        return "custom";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.SYSTEM;
    }

    @Override
    public String getDescription() {
        String usage = argumentsExecutors.stream()
                .map(executor -> getCharacter() + "custom " + executor.getCommand() + " (" + executor.getDescription() + ")\n")
                .reduce((message, executorInfos) -> message += executorInfos)
                .orElse("");

        return "Permet de créer une command custom \n"
                + usage; // can use TextFormatter here, but I don't think that would help
    }

    @Override
    public boolean isAuthorizedMember(Member member) {
        return member.hasPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.GUILD;
    }

    @Override
    public void execute(Message message, String[] args) {
        if (args.length == 0) {
            help(message);
            return;
        }

        Optional<CommandExecutor> optionalCommandExecutor = this.argumentsExecutors.stream()
                .filter(commandExecutor -> commandExecutor.getCommand().equalsIgnoreCase(args[0]))
                .filter(commandExecutor -> commandExecutor.canExecute(message))
                .findAny();

        if (optionalCommandExecutor.isPresent()) {
            String[] arrayWithoutFirstElement = Arrays.copyOfRange(args, 1, args.length);
            optionalCommandExecutor.get().execute(message, arrayWithoutFirstElement);
            return;
        }
        help(message);
    }

    private void help(Message message) {
        MessageEmbed messageEmbed = Utils.errorArguments(getCommand(), "<set,remove> <commande> [valeur]");
        message.getChannel().sendMessage(messageEmbed).queue();
    }
}
