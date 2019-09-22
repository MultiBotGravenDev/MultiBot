package fr.gravendev.multibot;

import fr.gravendev.multibot.commands.CommandManager;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.DatabaseConnectionBuilder;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.events.MultiBotListener;
import fr.gravendev.multibot.polls.PollsManager;
import fr.gravendev.multibot.quiz.QuizManager;
import fr.gravendev.multibot.quiz.WelcomeMessagesSetManager;
import fr.gravendev.multibot.utils.json.Configuration;
import fr.gravendev.multibot.utils.json.FileWriter;
import fr.gravendev.multibot.utils.json.Serializer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

class MultiBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiBot.class);

    private Configuration configuration;
    private final DAOManager daoManager;
    private final CommandManager commandManager;
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

        DatabaseConnection databaseConnection = DatabaseConnectionBuilder
                .aDatabaseConnection()
                .withHost(configuration.getHost())
                .withUser(configuration.getUser())
                .withPassword(configuration.getPassword())
                .withDatabase(configuration.getDatabase())
                .build();

        this.daoManager = new DAOManager(databaseConnection);

        this.quizManager = new QuizManager(daoManager);
        this.welcomeMessagesSetManager = new WelcomeMessagesSetManager(daoManager);
        this.pollsManager = new PollsManager(daoManager);
        this.commandManager = new CommandManager(configuration.getPrefix(), daoManager, welcomeMessagesSetManager, pollsManager);
    }

    void start() {
        try {

            this.jda = new JDABuilder(configuration.getToken())
                    .addEventListeners(new MultiBotListener(commandManager, daoManager, quizManager, welcomeMessagesSetManager, pollsManager))
                    .build();

            //SparkAPI sparkAPI = new SparkAPI(jda, databaseConnection);
            //sparkAPI.initRoutes();
            // TODO: Finish the website kappa

            LOGGER.info("Bot connected");

        } catch (LoginException ex) {
            ex.printStackTrace();
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
