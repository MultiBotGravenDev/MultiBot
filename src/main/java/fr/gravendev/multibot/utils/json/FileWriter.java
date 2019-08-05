package fr.gravendev.multibot.utils.json;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

public class FileWriter {

    private FileWriter(){}

    public static void writeFile(File file, String content) {

        final BufferedWriter fw;

        try {
            file.createNewFile();

            fw = new BufferedWriter(new java.io.FileWriter(file));
            fw.write(content);
            fw.flush();
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}