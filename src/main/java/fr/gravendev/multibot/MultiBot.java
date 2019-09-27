package fr.gravendev.multibot;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import fr.gravendev.multibot.commands.CommandManager;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.database.DatabaseConnectionBuilder;
import fr.gravendev.multibot.database.dao.DAOManager;
import fr.gravendev.multibot.events.MultiBotListener;
import fr.gravendev.multibot.polls.PollsManager;
import fr.gravendev.multibot.quiz.QuizManager;
import fr.gravendev.multibot.quiz.WelcomeMessagesSetManager;
import fr.gravendev.multibot.utils.Configuration;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

class MultiBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiBot.class);

    private final CommentedFileConfig config;
    private final DAOManager daoManager;
    private final CommandManager commandManager;
    private final QuizManager quizManager;
    private final WelcomeMessagesSetManager welcomeMessagesSetManager;
    private final PollsManager pollsManager;

    private JDA jda;

    MultiBot() {
        this.config = CommentedFileConfig.builder("configuration.toml")
                .defaultResource("/configuration.toml")
                .autosave()
                .build();
        this.config.load();
        for (Configuration configuration : Configuration.values()) {
            String path = configuration.getPath();
            String value = config.get(path).toString();
            configuration.setValue(value);
        }

        DatabaseConnection databaseConnection = DatabaseConnectionBuilder
                .aDatabaseConnection()
                .withHost(Configuration.DB_HOST.getValue())
                .withUser(Configuration.DB_USERNAME.getValue())
                .withPassword(Configuration.DB_PASSWORD.getValue())
                .withDatabase(Configuration.DB_DATABASE.getValue())
                .build();

        this.daoManager = new DAOManager(databaseConnection);

        this.quizManager = new QuizManager(daoManager);
        this.welcomeMessagesSetManager = new WelcomeMessagesSetManager(daoManager);
        this.pollsManager = new PollsManager();
        this.commandManager = new CommandManager(daoManager, welcomeMessagesSetManager, pollsManager);
    }

    void start() {
        try {

            this.jda = new JDABuilder(Configuration.TOKEN.getValue())
                    .addEventListeners(new MultiBotListener( commandManager, daoManager, quizManager, welcomeMessagesSetManager, pollsManager))
                    .setActivity(Activity.listening("\"il sort quand le multibot?\""))
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
