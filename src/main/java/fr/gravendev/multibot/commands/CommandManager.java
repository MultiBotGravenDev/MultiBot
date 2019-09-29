package fr.gravendev.multibot.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.commands.commands.CustomCommand;
import fr.gravendev.multibot.commands.commands.HelpCommand;
import fr.gravendev.multibot.commands.commands.RedescendsCommand;
import fr.gravendev.multibot.commands.commands.UserinfoCommand;
import fr.gravendev.multibot.database.dao.CustomCommandDAO;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.database.data.CustomCommandData;
import fr.gravendev.multibot.moderation.commands.AntiCommand;
import fr.gravendev.multibot.moderation.commands.BadWordsCommand;
import fr.gravendev.multibot.moderation.commands.BanCommand;
import fr.gravendev.multibot.moderation.commands.BanInfoCommand;
import fr.gravendev.multibot.moderation.commands.ImmuniseCommand;
import fr.gravendev.multibot.moderation.commands.InfractionsCommand;
import fr.gravendev.multibot.moderation.commands.KickCommand;
import fr.gravendev.multibot.moderation.commands.MuteCommand;
import fr.gravendev.multibot.moderation.commands.MuteInfoCommand;
import fr.gravendev.multibot.moderation.commands.TempbanCommand;
import fr.gravendev.multibot.moderation.commands.TempmuteCommand;
import fr.gravendev.multibot.moderation.commands.UnbanCommand;
import fr.gravendev.multibot.moderation.commands.UnmuteCommand;
import fr.gravendev.multibot.moderation.commands.WarnCommand;
import fr.gravendev.multibot.polls.PollsManager;
import fr.gravendev.multibot.polls.commands.PollCommand;
import fr.gravendev.multibot.quiz.WelcomeMessagesSetManager;
import fr.gravendev.multibot.quiz.commands.QuizCommand;
import fr.gravendev.multibot.rank.RankCommand;
import fr.gravendev.multibot.roles.commands.RolesCommand;
import fr.gravendev.multibot.utils.Configuration;
import fr.gravendev.multibot.votes.VoteCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

// TODO Refactor to remove else and to have 1 level of indentation, etc.
public class CommandManager {

    private final char prefix;
    private List<CommandExecutor> commandExecutors;
    private final CustomCommandDAO customCommandDAO;

    public CommandManager(DAOManager daoManager, WelcomeMessagesSetManager welcomeMessagesSetManager, PollsManager pollsManager) {
        this.prefix = Configuration.PREFIX.getValue().charAt(0);
        commandExecutors = new ArrayList<>(Arrays.asList(
                new CustomCommand(daoManager),
                new UserinfoCommand(),
                new RedescendsCommand(),

                new AntiCommand(daoManager),
                new BanCommand(daoManager),
                new BanInfoCommand(daoManager),
                new InfractionsCommand(daoManager),
                new KickCommand(daoManager),
                new MuteCommand(daoManager),
                new MuteInfoCommand(daoManager),
                new TempbanCommand(daoManager),
                new TempmuteCommand(daoManager),
                new UnbanCommand(daoManager),
                new UnmuteCommand(daoManager),
                new WarnCommand(daoManager),
                new BadWordsCommand(daoManager),
                new ImmuniseCommand(daoManager),

                new QuizCommand(daoManager, welcomeMessagesSetManager),
                new RankCommand(daoManager),
                new RolesCommand(daoManager),
                new VoteCommand(daoManager),

                new PollCommand(pollsManager)
        ));
        this.commandExecutors.add(new HelpCommand(commandExecutors, daoManager));
        this.customCommandDAO = daoManager.getCustomCommandDAO();
    }

    private void executeIfAble(CommandExecutor commandExecutor, Message message, String[] args) {
        if (commandExecutor.canExecute(message)) {
            int argsLength = args.length;
            // TODO Find a better name for that
            String[] stringArray = Arrays.copyOfRange(args, 1, argsLength);

            commandExecutor.execute(message, stringArray);
        }
    }

    void executeCommand(Message message) {
        String content = message.getContentRaw();

        if (content.length() == 0) {
            return;
        }

        char firstContentChar = content.charAt(0);

        if (firstContentChar != prefix) {
            return;
        }

        String contentWithoutFirstChar = content.substring(1);
        String[] args = contentWithoutFirstChar.split(" +");

        Optional<CommandExecutor> optionalCommandExecutor = this.commandExecutors.stream()
                .filter(commandExecutor -> commandExecutor.getCommand().equalsIgnoreCase(args[0]))
                .findAny();

        MessageChannel channel = message.getChannel();

        if (optionalCommandExecutor.isPresent()) {
            CommandExecutor commandExecutor = optionalCommandExecutor.get();
            executeIfAble(commandExecutor, message, args);
            return;
        }
        if (args[0].matches("[0-9]+")) {
            return;
        }

        CustomCommandData customCommandData = customCommandDAO.get(args[0]);

        if (customCommandData != null) {
            String customCommandDataText = customCommandData.getText();

            channel.sendMessage(customCommandDataText).queue();
        }

    }

}
