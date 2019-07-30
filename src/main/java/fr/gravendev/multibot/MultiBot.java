package fr.gravendev.multibot;

import fr.gravendev.multibot.commands.CommandManager;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.DatabaseConnectionBuilder;
import fr.gravendev.multibot.events.MultiBotListener;
import fr.gravendev.multibot.polls.PollsManager;
import fr.gravendev.multibot.quiz.QuizManager;
import fr.gravendev.multibot.quiz.WelcomeMessagesSetManager;
import fr.gravendev.multibot.spark.SparkAPI;
import fr.gravendev.multibot.utils.json.Configuration;
import fr.gravendev.multibot.utils.json.FileWriter;
import fr.gravendev.multibot.utils.json.Serializer;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

import javax.security.auth.login.LoginException;

class MultiBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiBot.class);

    private Configuration configuration;
    private final CommandManager commandManager;
    private final DatabaseConnection databaseConnection;
    private final QuizManager quizManager;
    private final WelcomeMessagesSetManager welcomeMessagesSetManager;
    private final PollsManager pollsManager;

    private JDA jda;

    MultiBot() {
        this.configuration = new Serializer<Configuration>().deserialize(Configuration.CONFIGURATION_FILE, Configuration.class);

        if (this.configuration == null) {
            this.configuration = new Configuration();
            String json = new Serializer<Configuration>().serialize(configuration);
            FileWriter.writeFile(Configuration.CONFIGURATION_FILE, json);
        }

        this.databaseConnection = DatabaseConnectionBuilder
                .aDatabaseConnection()
                .withHost(configuration.getHost())
                .withUser(configuration.getUser())
                .withPassword(configuration.getPassword())
                .withDatabase(configuration.getDatabase())
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

            SparkAPI sparkAPI = new SparkAPI(jda, databaseConnection);
            sparkAPI.initRoutes();

            LOGGER.info("Bot connected");

        } catch (LoginException e) {
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
