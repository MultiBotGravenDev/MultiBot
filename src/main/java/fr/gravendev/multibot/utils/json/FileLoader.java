package fr.gravendev.multibot.utils.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class FileLoader {

    private FileLoader() {
    }

    static String load(File file) {

        final StringBuilder stringBuilder = new StringBuilder();

        try {
            file.createNewFile();
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder
                        .append(line)
                        .append("\n");
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();

    }

}
