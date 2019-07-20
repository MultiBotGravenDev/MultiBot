package fr.gravendev.multibot.quiz.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QuizCommand implements CommandExecutor {

    private final List<CommandExecutor> argumentExecutors;

    public QuizCommand(DatabaseConnection databaseConnection) {
        this.argumentExecutors = Arrays.asList(
                new HereCommand(databaseConnection),
                new ChannelCommand(databaseConnection),
                new SetCommand(databaseConnection),
                new ListCommand(databaseConnection)
        );
    }

    @Override
    public String getCommand() {
        return "quiz";
    }

    @Override
    public String getDescription() {
        return "envoie le message de bienvenue et la réaction pour recevoir le formulaire";
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.GUILD;
    }

    @Override
    public List<String> getAuthorizedChannelsNames() {
        return Arrays.asList("lisez-ce-salon", "piliers");
    }

    @Override
    public boolean isAuthorizedMember(Member member) {
        return member.hasPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(Message message, String[] args) {

        if (args.length == 0) {
            message.getChannel().sendMessage("commande inconnue. "
                    + "!quiz ["
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

}
