package fr.gravendev.multibot;

import fr.gravendev.multibot.json.Configuration;
import fr.gravendev.multibot.json.FileWriter;
import fr.gravendev.multibot.json.Serializer;

public class MultiBot {

    private final Configuration CONFIGURATION = new Serializer<Configuration>().deserialize(Configuration.CONFIGURATION_FILE, Configuration.class);

    private MultiBot() {


        String configurationJson = new Serializer<Configuration>().serialize(CONFIGURATION);
        FileWriter.writeFile(Configuration.CONFIGURATION_FILE, configurationJson);
    }

}
