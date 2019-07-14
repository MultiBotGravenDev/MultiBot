package fr.gravendev.multibot;

import fr.gravendev.multibot.commands.CommandManager;
import fr.gravendev.multibot.database.DatabaseConnection;
import fr.gravendev.multibot.events.MultiBotListener;
import fr.gravendev.multibot.json.Configuration;
import fr.gravendev.multibot.json.FileWriter;
import fr.gravendev.multibot.json.Serializer;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class MultiBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiBot.class);

    private final Configuration configuration = new Serializer<Configuration>().deserialize(Configuration.CONFIGURATION_FILE, Configuration.class);
    private JDA jda;
    private final CommandManager commandManager = new CommandManager(configuration.getPrefix());

    private final DatabaseConnection databaseConnection;

    private MultiBot() {

        databaseConnection = new DatabaseConnection("root", "password", "multibot");

        try {
            buildJDA();
        } catch (LoginException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);
        while (!scanner.nextLine().equalsIgnoreCase("stop")) {}

        stop();
    }

    private void buildJDA() throws LoginException {
        JDABuilder builder = new JDABuilder(configuration.getToken())
                .addEventListener(new MultiBotListener(this.commandManager));
        jda = builder.build();
        LOGGER.info("Bot connected");
    }

    private void stop() {
        String configurationJson = new Serializer<Configuration>().serialize(configuration);
        FileWriter.writeFile(Configuration.CONFIGURATION_FILE, configurationJson);
        jda.shutdown();
        LOGGER.info("Bot disconnected");
        System.exit(0);
    }

    public Connection getConnection() throws SQLException {
        return databaseConnection.getConnection();
    }

    public static void main(String[] args) {
        new MultiBot();
    }

}
