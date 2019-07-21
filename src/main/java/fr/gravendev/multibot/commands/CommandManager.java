package fr.gravendev.multibot.commands;

import fr.gravendev.multibot.commands.commands.AboutCommand;
import fr.gravendev.multibot.commands.commands.CommandExecutor;
import fr.gravendev.multibot.commands.commands.CustomCommand;
import fr.gravendev.multibot.database.data.CustomCommandData;
import fr.gravendev.multibot.moderation.*;
import fr.gravendev.multibot.database.dao.CustomCommandDAO;
import fr.gravendev.multibot.moderation.commands.AntiCommand;
import fr.gravendev.multibot.quiz.WelcomeMessagesSetManager;
import fr.gravendev.multibot.rank.RankCommand;
import fr.gravendev.multibot.quiz.commands.QuizCommand;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.roles.commands.RolesCommand;
import fr.gravendev.multibot.commands.commands.HelpCommand;
import fr.gravendev.multibot.commands.commands.UserinfoCommand;
import fr.gravendev.multibot.votes.VoteCommand;
import net.dv8tion.jda.core.entities.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CommandManager {

    private List<CommandExecutor> commandExecutors;
    private final DatabaseConnection databaseConnection;
    private final char prefix;

    public CommandManager(char prefix, DatabaseConnection databaseConnection, WelcomeMessagesSetManager welcomeMessagesSetManager) {
        this.prefix = prefix;
        commandExecutors = new ArrayList<>(Arrays.asList(
                new AboutCommand(),
                new CustomCommand(databaseConnection),
                new UserinfoCommand(),
                new AntiCommand(databaseConnection),
                new BanCommand(databaseConnection),
                new BanInfoCommand(databaseConnection),
                new InfractionsCommand(databaseConnection),
                new KickCommand(databaseConnection),
                new MuteCommand(databaseConnection),
                new TempmuteCommand(databaseConnection),
                new WarnCommand(databaseConnection),
                new QuizCommand(databaseConnection, welcomeMessagesSetManager),
                new RankCommand(databaseConnection),
                new RolesCommand(databaseConnection),
                new VoteCommand(databaseConnection)
        ));
        this.databaseConnection = databaseConnection;
        this.commandExecutors.add(new HelpCommand(this.commandExecutors, databaseConnection));
    }

    void executeCommand(Message message) {

        String content = message.getContentRaw();

        if (content.length() == 0 || content.charAt(0) != this.prefix) return;

        String[] args = content.substring(1).split(" +");

        Optional<CommandExecutor> optionalCommandExecutor = this.commandExecutors.stream()
                .filter(commandExecutor -> commandExecutor.getCommand().equalsIgnoreCase(args[0]))
                .filter(commandExecutor -> commandExecutor.canExecute(message))
                .findAny();

        if (optionalCommandExecutor.isPresent()) {
            optionalCommandExecutor.get().execute(message, Arrays.copyOfRange(args, 1, args.length));
        } else {

            CustomCommandData customCommandData = new CustomCommandDAO(this.databaseConnection).get(args[0]);

            if (customCommandData != null) {
                message.getChannel().sendMessage(customCommandData.text).queue();
            }

        }

    }

}
