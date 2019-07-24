package fr.gravendev.multibot.utils.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

public class Serializer<T> {

    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    public String serialize(T object) {
        return GSON.toJson(object);
    }

    public T deserialize(File file, Class<T> tClass) {
        String fileContent = FileLoader.load(file);
        return GSON.fromJson(fileContent, tClass);
    }

}
