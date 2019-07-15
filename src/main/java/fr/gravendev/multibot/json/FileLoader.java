package fr.gravendev.multibot.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileLoader {

    private FileLoader() {}

    public static String load(File file) {

        final StringBuilder text = new StringBuilder();

        try {
            file.createNewFile();

            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;

            while ((line = reader.readLine()) != null) {
                text.append(line).append("\n");
            }

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return text.toString();

    }

}
