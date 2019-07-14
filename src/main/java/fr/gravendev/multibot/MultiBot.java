package fr.gravendev.multibot;

import fr.gravendev.multibot.events.MultiBotListener;
import fr.gravendev.multibot.json.Configuration;
import fr.gravendev.multibot.json.FileWriter;
import fr.gravendev.multibot.json.Serializer;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.Scanner;

public class MultiBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiBot.class);

    private final Configuration CONFIGURATION = new Serializer<Configuration>().deserialize(Configuration.CONFIGURATION_FILE, Configuration.class);

    private JDA jda;

    private MultiBot() throws LoginException {

        JDABuilder builder = new JDABuilder(CONFIGURATION.token);
        jda = builder.build();

        LOGGER.info("Bot connected");

        jda.addEventListener(new MultiBotListener());

        Scanner scanner = new Scanner(System.in);
        while (!scanner.nextLine().equalsIgnoreCase("stop")) {}

        String configurationJson = new Serializer<Configuration>().serialize(CONFIGURATION);
        FileWriter.writeFile(Configuration.CONFIGURATION_FILE, configurationJson);
        jda.shutdown();
        LOGGER.info("Bot disconnected");
        System.exit(0);
    }

    public static void main(String[] args) {
        try {
            new MultiBot();
        }catch (LoginException ex) {
            ex.printStackTrace();
        }
    }

}
