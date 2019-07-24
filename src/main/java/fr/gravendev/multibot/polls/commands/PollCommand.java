package fr.gravendev.multibot.polls.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.polls.PollsManager;
import net.dv8tion.jda.core.entities.Message;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PollCommand implements CommandExecutor {

    private final List<CommandExecutor> argumentsExecutors;

    public PollCommand(PollsManager pollsManager) {
        argumentsExecutors = Arrays.asList(
                new StartCommand(pollsManager),
                new ColorCommand(pollsManager),
                new AskCommand(pollsManager),
                new ChoiceCommand(pollsManager),
                new EmoteCommand(pollsManager),
                new FinishCommand(pollsManager),
                new CancelCommand(pollsManager)
        );
    }

    @Override
    public String getCommand() {
        return "poll";
    }

    @Override
    public String getDescription() {
        return "Commandes relatives aux sondages. \n"
                + this.argumentsExecutors
                .stream()
                .map(executor -> "!poll " + executor.getCommand() + " (" + executor.getDescription() + ")\n")
                .reduce((message, executorInfos) -> message += executorInfos)
                .orElse("");
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.ALL;
    }

    @Override
    public List<String> getAuthorizedChannelsNames() {
        return Collections.singletonList("commandes");
    }

    @Override
    public void execute(Message message, String[] args) {

        if (args.length == 0) {
            message.getChannel().sendMessage("Erreur. "
                    + "!poll ["
                    + this.argumentsExecutors.stream().map(CommandExecutor::getCommand).collect(Collectors.joining("/"))
                    + "]").queue();
            return;
        }

        this.argumentsExecutors.stream()
                .filter(commandExecutor -> commandExecutor.getCommand().equalsIgnoreCase(args[0]))
                .findAny()
                .ifPresent(commandExecutor -> commandExecutor.execute(message, Arrays.copyOfRange(args, 1, args.length)));

    }

}
