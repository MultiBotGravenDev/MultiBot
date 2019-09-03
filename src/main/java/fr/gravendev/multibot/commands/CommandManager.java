package fr.gravendev.multibot.commands;

import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.commands.commands.CustomCommand;
import fr.gravendev.multibot.commands.commands.HelpCommand;
import fr.gravendev.multibot.commands.commands.UserinfoCommand;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.dao.CustomCommandDAO;
import fr.gravendev.multibot.database.data.CustomCommandData;
import fr.gravendev.multibot.moderation.commands.BadWordCommand;
import fr.gravendev.multibot.moderation.commands.*;
import fr.gravendev.multibot.polls.PollsManager;
import fr.gravendev.multibot.polls.commands.PollCommand;
import fr.gravendev.multibot.quiz.WelcomeMessagesSetManager;
import fr.gravendev.multibot.quiz.commands.QuizCommand;
import fr.gravendev.multibot.rank.RankCommand;
import fr.gravendev.multibot.roles.commands.RolesCommand;
import fr.gravendev.multibot.votes.VoteCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

// TODO Refactor to remove else and to have 1 level of indentation, etc.
public class CommandManager {

    private final DatabaseConnection databaseConnection;
    private final char prefix;
    private List<CommandExecutor> commandExecutors;

    public CommandManager(char prefix, DatabaseConnection databaseConnection, WelcomeMessagesSetManager welcomeMessagesSetManager, PollsManager pollsManager) {
        this.prefix = prefix;
        commandExecutors = new ArrayList<>(Arrays.asList(
                new CustomCommand(databaseConnection),
                new UserinfoCommand(),

                new AntiCommand(databaseConnection),
                new BanCommand(databaseConnection),
                new BanInfoCommand(databaseConnection),
                new InfractionsCommand(databaseConnection),
                new KickCommand(databaseConnection),
                new MuteCommand(databaseConnection),
                new MuteInfoCommand(databaseConnection),
                new TempbanCommand(databaseConnection),
                new TempmuteCommand(databaseConnection),
                new UnbanCommand(databaseConnection),
                new UnmuteCommand(databaseConnection),
                new WarnCommand(databaseConnection),
                new BadWordCommand(databaseConnection),

                new QuizCommand(databaseConnection, welcomeMessagesSetManager),
                new RankCommand(databaseConnection),
                new RolesCommand(databaseConnection),
                new VoteCommand(databaseConnection),

                new PollCommand(pollsManager)
        ));
        this.databaseConnection = databaseConnection;
        this.commandExecutors.add(new HelpCommand(commandExecutors, databaseConnection));
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

            if (commandExecutor.canExecute(message)) {
                int argsLength = args.length;
                // TODO Find a better name for that
                String[] stringArray = Arrays.copyOfRange(args, 1, argsLength);

                commandExecutor.execute(message, stringArray);
                return;
            }
            channel.sendMessage("Mauvais channel ou permission manquante.").queue();
            return;
        }
        if (args[0].matches("[0-9]+")) {
            return;
        }

        CustomCommandDAO customCommandDAO = new CustomCommandDAO(databaseConnection);
        CustomCommandData customCommandData = customCommandDAO.get(args[0]);

        if (customCommandData != null) {
            String customCommandDataText = customCommandData.getText();

            channel.sendMessage(customCommandDataText).queue();
        }

    }

}
