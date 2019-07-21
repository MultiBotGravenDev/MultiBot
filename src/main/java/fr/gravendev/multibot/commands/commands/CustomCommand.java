package fr.gravendev.multibot.commands.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.customs.RemoveCommand;
import fr.gravendev.multibot.commands.commands.customs.SetCommand;
import fr.gravendev.multibot.database.DatabaseConnection;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomCommand implements CommandExecutor {

    private final List<CommandExecutor> argumentsExecutors;

    public CustomCommand(DatabaseConnection databaseConnection) {
        this.argumentsExecutors = Arrays.asList(
                new SetCommand(databaseConnection),
                new RemoveCommand(databaseConnection)
        );
    }

    @Override
    public String getCommand() {
        return "custom";
    }

    @Override
    public String getDescription() {
        return "Permet de créer une command custom";
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
            message.getChannel().sendMessage("commande inconnue. "
                    + "!quiz ["
                    + this.argumentsExecutors.stream().map(CommandExecutor::getCommand).collect(Collectors.joining("/"))
                    + "]").queue();
            return;
        }

        this.argumentsExecutors.stream()
                .filter(commandExecutor -> commandExecutor.getCommand().equalsIgnoreCase(args[0]))
                .filter(commandExecutor -> commandExecutor.canExecute(message))
                .findAny()
                .ifPresent(commandExecutor -> commandExecutor.execute(message, Arrays.copyOfRange(args, 1, args.length)));

    }

}
