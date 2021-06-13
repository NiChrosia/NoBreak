package nichrosia.nobreak.client;

import com.google.gson.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class NoBreakConfig {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private boolean breaking = false;
    private boolean feedback = true;

    public static NoBreakConfig loadConfig(File file) {
        NoBreakConfig config;

        if (file.exists() && file.isFile()) {
            try (
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
            ) {
                config = gson.fromJson(bufferedReader, NoBreakConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load config", e);
            }
        } else {
            config = new NoBreakConfig();
        }

        config.saveConfig(file);

        return config;
    }

    public void saveConfig(File config) {
        try (
            FileOutputStream stream = new FileOutputStream(config);
            Writer writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)
        ) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public boolean isBreaking() {
        return breaking;
    }

    public boolean isFeedback() {
        return feedback;
    }
}
