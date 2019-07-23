package fr.gravendev.multibot;

import fr.gravendev.multibot.commands.CommandManager;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.DatabaseConnectionBuilder;
import fr.gravendev.multibot.events.MultiBotListener;
import fr.gravendev.multibot.polls.PollsManager;
import fr.gravendev.multibot.quiz.WelcomeMessagesSetManager;
import fr.gravendev.multibot.utils.json.Configuration;
import fr.gravendev.multibot.utils.json.Serializer;
import fr.gravendev.multibot.quiz.QuizManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

class MultiBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiBot.class);

    private final Configuration configuration;
    private final CommandManager commandManager;
    private final DatabaseConnection databaseConnection;
    private final QuizManager quizManager;
    private final WelcomeMessagesSetManager welcomeMessagesSetManager;
    private final PollsManager pollsManager;

    private JDA jda;

    MultiBot() {
        this.configuration = new Serializer<Configuration>().deserialize(Configuration.CONFIGURATION_FILE, Configuration.class);

        this.databaseConnection = DatabaseConnectionBuilder
                .aDatabaseConnection()
                .withhost(configuration.getHost())
                .withuser(configuration.getUser())
                .withpassword(configuration.getPassword())
                .withdatabase(configuration.getDatabase())
                .build();

        this.quizManager = new QuizManager(databaseConnection);
        this.welcomeMessagesSetManager = new WelcomeMessagesSetManager(databaseConnection);
        this.pollsManager = new PollsManager(databaseConnection);
        this.commandManager = new CommandManager(this.configuration.getPrefix(), databaseConnection, welcomeMessagesSetManager, pollsManager);
    }

    void start() {
        try {

            this.jda = new JDABuilder(configuration.getToken())
                    .addEventListener(new MultiBotListener(this.commandManager, this.databaseConnection, this.quizManager, this.welcomeMessagesSetManager, pollsManager))
                    .build();

            LOGGER.info("Bot connected");

        } catch (LoginException e) {
            e.printStackTrace();
            LOGGER.error("Failed to connect the bot");
            System.exit(0);
        }
    }

    void stop() {
        jda.shutdown();
        LOGGER.info("Bot disconnected");
        System.exit(0);
    }

}
