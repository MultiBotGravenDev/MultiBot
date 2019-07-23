package fr.gravendev.multibot.polls.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.polls.PollsManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PollCommand implements CommandExecutor {

    private final PollsManager pollsManager;
    private final List<CommandExecutor> argumentsExecutors;

    public PollCommand(PollsManager pollsManager) {
        this.pollsManager = pollsManager;
        argumentsExecutors = Arrays.asList(
                new ColorCommand(this.pollsManager),
                new AskCommand(this.pollsManager),
                new ChoiceCommand(this.pollsManager),
                new EmoteCommand(this.pollsManager),
                new FinishCommand(this.pollsManager)
        );
    }

    @Override
    public String getCommand() {
        return "poll";
    }

    @Override
    public String getDescription() {
        return "Commandes relatives aux sondages. \n"
                + "!poll (Permet de créer un sondage)\n"
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
        return Collections.singletonList("_commandes");
    }

    @Override
    public void execute(Message message, String[] args) {

        if (args.length == 0) {
            message.getAuthor().openPrivateChannel().queue(privateChannel ->
                    new MessageBuilder()
                            .setContent("!help pour voir la liste des commandes disponible")
                            .setEmbed(new EmbedBuilder()
                                    .setTitle(" ")
                                    .setFooter(message.getAuthor().getName(), message.getAuthor().getAvatarUrl())
                                    .build())
                            .sendTo(privateChannel)
                            .queue(message1 -> this.pollsManager.registerPoll(message.getAuthor(), message1.getIdLong())));
            return;
        }

        this.argumentsExecutors.stream()
                .filter(commandExecutor -> commandExecutor.getCommand().equalsIgnoreCase(args[0]))
                .findAny()
                .ifPresentOrElse(commandExecutor -> commandExecutor.execute(message, Arrays.copyOfRange(args, 1, args.length)),
                        () -> message.getChannel().sendMessage("Erreur. "
                                + "!poll ["
                                + this.argumentsExecutors.stream().map(CommandExecutor::getCommand).collect(Collectors.joining("/"))
                                + "]").queue());

    }

}
