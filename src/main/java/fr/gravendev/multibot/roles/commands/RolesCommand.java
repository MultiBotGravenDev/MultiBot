package fr.gravendev.multibot.roles.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandCategory;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RolesCommand implements CommandExecutor {

    private final List<CommandExecutor> argumentExecutors;

    public RolesCommand(DatabaseConnection databaseConnection) {
        this.argumentExecutors = Arrays.asList(
                new AddCommand(databaseConnection),
                new RemoveCommand(databaseConnection),
                new ListCommand(databaseConnection),
                new HereCommand(databaseConnection),
                new MessageCommand(databaseConnection)
        );
    }

    @Override
    public String getCommand() {
        return "roles";
    }

    @Override
    public String getDescription() {
        return "Commandes relatives aux rôles langages. \n"
                + this.argumentExecutors
                .stream()
                .map(executor -> "!roles " + executor.getCommand() + " (" + executor.getDescription() + ")\n")
                .reduce((message, executorInfos) -> message += executorInfos)
                .orElse("");
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.SYSTEM;
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.GUILD;
    }

    @Override
    public void execute(Message message, String[] args) {

        if (args.length == 0) {
            message.getChannel().sendMessage("Erreur. "
                    + "!roles ["
                    + this.argumentExecutors.stream().map(CommandExecutor::getCommand).collect(Collectors.joining("/"))
                    + "]").queue();
            return;
        }

        this.argumentExecutors.stream()
                .filter(commandExecutor -> commandExecutor.getCommand().equalsIgnoreCase(args[0]))
                .filter(commandExecutor -> commandExecutor.canExecute(message))
                .findAny()
                .ifPresent(commandExecutor -> commandExecutor.execute(message, Arrays.copyOfRange(args, 1, args.length)));

    }

    @Override
    public List<String> getAuthorizedChannelsNames() {
        return Arrays.asList("rôle-langage-test", "piliers");
    }

    @Override
    public boolean isAuthorizedMember(Member member) {
        return member.hasPermission(Permission.ADMINISTRATOR);
    }

}
