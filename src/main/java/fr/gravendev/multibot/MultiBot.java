package fr.gravendev.multibot;

import fr.gravendev.multibot.json.Configuration;
import fr.gravendev.multibot.json.FileWriter;
import fr.gravendev.multibot.json.Serializer;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import javax.security.auth.login.LoginException;

public class MultiBot {

    private final Configuration CONFIGURATION = new Serializer<Configuration>().deserialize(Configuration.CONFIGURATION_FILE, Configuration.class);

    private JDA jda;

    private MultiBot() throws LoginException {

        JDABuilder builder = new JDABuilder(CONFIGURATION.token);
        jda = builder.build();

        String configurationJson = new Serializer<Configuration>().serialize(CONFIGURATION);
        FileWriter.writeFile(Configuration.CONFIGURATION_FILE, configurationJson);
    }

    public static void main(String[] args) {
        try {
            new MultiBot();
        }catch (LoginException ex) {
            ex.printStackTrace();
        }
    }

}
