package fr.gravendev.multibot;

import fr.gravendev.multibot.commands.CommandManager;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.events.MultiBotListener;
import fr.gravendev.multibot.json.Configuration;
import fr.gravendev.multibot.json.Serializer;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.Scanner;

public class MultiBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiBot.class);

    private final Configuration configuration;
    private JDA jda;
    private final CommandManager commandManager;
    private final DatabaseConnection databaseConnection;

    private MultiBot() {

        this.configuration = new Serializer<Configuration>().deserialize(Configuration.CONFIGURATION_FILE, Configuration.class);
        databaseConnection = new DatabaseConnection(configuration.getHost(), configuration.getUsername(), configuration.getPassword(), configuration.getDatabase());
        this.commandManager = new CommandManager(this.configuration.getPrefix(), databaseConnection);

        try {
            buildJDA();
        } catch (LoginException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);
        while (!scanner.nextLine().equalsIgnoreCase("stop")) {
            LOGGER.info("write \"stop\" to stop the bot");
        }

        stop();
    }

    private void buildJDA() throws LoginException {
        this.jda = new JDABuilder(configuration.getToken())
                .addEventListener(new MultiBotListener(this.commandManager, databaseConnection))
                .build();
        LOGGER.info("Bot connected");
    }

    private void stop() {
        jda.shutdown();
        LOGGER.info("Bot disconnected");
        System.exit(0);
    }

    public static void main(String[] args) {
        new MultiBot();
    }

}
