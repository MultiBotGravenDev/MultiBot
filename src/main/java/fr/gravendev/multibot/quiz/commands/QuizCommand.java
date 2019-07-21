package fr.gravendev.multibot.quiz.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.quiz.WelcomeMessagesSetManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QuizCommand implements CommandExecutor {

    private final List<CommandExecutor> argumentExecutors;

    public QuizCommand(DatabaseConnection databaseConnection, WelcomeMessagesSetManager welcomeMessagesSetManager) {
        this.argumentExecutors = Arrays.asList(
                new HereCommand(databaseConnection),
                new ChannelCommand(databaseConnection),
                new SetCommand(databaseConnection, welcomeMessagesSetManager),
                new ListCommand(databaseConnection)
        );
    }

    @Override
    public String getCommand() {
        return "quiz";
    }

    @Override
    public String getDescription() {
        return "Commandes relatives au quizz d'entrée sur le serveur. \n"
                + this.argumentExecutors
                .stream()
                .map(executor -> "!quiz " + executor.getCommand() + " (" + executor.getDescription() + ")\n")
                .reduce((message, executorInfos) -> message += executorInfos)
                .orElse("");
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
            message.getChannel().sendMessage("Erreur. "
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
