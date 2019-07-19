package fr.gravendev.multibot;

import fr.gravendev.multibot.commands.CommandManager;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.events.MultiBotListener;
import fr.gravendev.multibot.tasks.AntiRolesTask;
import fr.gravendev.multibot.utils.json.Configuration;
import fr.gravendev.multibot.utils.json.Serializer;
import fr.gravendev.multibot.quiz.QuizManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.Timer;

class MultiBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiBot.class);

    private final Configuration configuration;
    private final CommandManager commandManager;
    private final DatabaseConnection databaseConnection;
    private final QuizManager quizManager;

    private JDA jda;

    MultiBot() {
        this.configuration = new Serializer<Configuration>().deserialize(Configuration.CONFIGURATION_FILE, Configuration.class);
        this.databaseConnection = new DatabaseConnection(configuration.getHost(), configuration.getUsername(), configuration.getPassword(), configuration.getDatabase());
        this.commandManager = new CommandManager(this.configuration.getPrefix(), databaseConnection);
        this.quizManager = new QuizManager(databaseConnection);
    }

    void start() {
        try {

            this.jda = new JDABuilder(configuration.getToken())
                    .addEventListener(new MultiBotListener(this.commandManager, this.databaseConnection, this.quizManager))
                    .build();

            LOGGER.info("Bot connected");

            new Timer().schedule(new AntiRolesTask(this.jda, this.databaseConnection), 0, 10_000);

        } catch (LoginException e) {
            e.printStackTrace();
            LOGGER.error("Failed to connect the bot");
        }
    }

    void stop() {
        jda.shutdown();
        LOGGER.info("Bot disconnected");
        System.exit(0);
    }

}
