package app.aplan.burden;

import app.aplan.burden.config.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Joiner;
import com.google.common.io.LineReader;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static List<String> call(String command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(Joiner.on(" ").join(new String[]{"cmd.exe", "/c", "chcp", "65001", "&&", command}));
        LineReader reader = new LineReader(new InputStreamReader(process.getInputStream()));
        List<String> lines = new ArrayList<>();
        String line;
        for (; ; ) {
            line = reader.readLine();
            if (line == null) {
                break;
            }
            lines.add(line);
        }
        process.waitFor();
        return lines.subList(1, lines.size());
    }

    public static Path userHome() {
        return Paths.get(System.getProperty("user.home"));
    }

    public static Path configFile() {
        return userHome().resolve(".burden.yaml");
    }

    public static Configuration readConfig() {
        try {
            return new ObjectMapper(new YAMLFactory())
                    .readValue(configFile().toFile(), Configuration.class);
        } catch (IOException e) {
            return new Configuration();
        }
    }

    public static void writeConfig(Configuration configuration) {
        try {
            new ObjectMapper(new YAMLFactory()).writeValue(configFile().toFile(), configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
