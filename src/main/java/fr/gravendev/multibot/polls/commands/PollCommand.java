package fr.gravendev.multibot.polls.commands;

import fr.gravendev.multibot.commands.ChannelType;
import fr.gravendev.multibot.commands.commands.CommandCategory;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.polls.PollsManager;
import fr.gravendev.multibot.utils.GuildUtils;
import fr.gravendev.multibot.utils.Utils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PollCommand implements CommandExecutor {

    private final List<CommandExecutor> argumentsExecutors;

    public PollCommand(PollsManager pollsManager) {

        argumentsExecutors = new ArrayList<CommandExecutor>() {{
            add(new ColorCommand(pollsManager));
            add(new AskCommand(pollsManager));
            add(new ChoiceCommand(pollsManager));
            add(new EmoteCommand(pollsManager));
            add(new FinishCommand(pollsManager));
            add(new CancelCommand(pollsManager));
            add(new StartCommand(pollsManager, this));
        }};
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
                .map(executor -> getCharacter()+"poll " + executor.getCommand() + " (" + executor.getDescription() + ")\n")
                .reduce((message, executorInfos) -> message += executorInfos)
                .orElse("");
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.UTILS;
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
    public boolean isAuthorizedMember(Member member) {
        return GuildUtils.hasRole(member, "Sondeur");
    }

    @Override
    public void execute(Message message, String[] args) {

        if (args.length == 0) {
            message.getChannel().sendMessage(Utils.errorArguments(getCommand(), Arrays.toString(this.argumentsExecutors.stream().map(CommandExecutor::getCommand).toArray()))).queue();
            return;
        }

        Optional<CommandExecutor> optionalCommandExecutor = this.argumentsExecutors.stream()
                .filter(commandExecutor -> commandExecutor.getCommand().equalsIgnoreCase(args[0]))
                .findAny();

        if(optionalCommandExecutor.isPresent()) {
            optionalCommandExecutor.get().execute(message, Arrays.copyOfRange(args, 1, args.length));
            return;
        }

        message.getChannel().sendMessage("Erreur. "
                + getCharacter() + "poll ["
                + this.argumentsExecutors.stream().map(CommandExecutor::getCommand).collect(Collectors.joining("/"))
                + "]").queue();

    }

}
