package app.aplan.burden;

import app.aplan.burden.config.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Joiner;
import com.google.common.io.LineReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Utils {
    public static List<String> call(String command) throws IOException, InterruptedException {
        List<String> lines = new ArrayList<>();
        call(command, lines::add, null);
        return lines.subList(1, lines.size());
    }

    public static void call(String command, Consumer<String> input, Consumer<String> error) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(Joiner.on(" ").join(new String[]{"cmd.exe", "/c", "chcp", "65001", "&&", command}));
        if (input != null) {
            readline(process.getInputStream(), input);
        }
        if (error != null) {
            readline(process.getErrorStream(), error);
        }
        process.waitFor();
    }

    public static void callInThread(String command, Consumer<String> input, Consumer<String> error) throws IOException, InterruptedException {
        AtomicInteger count = new AtomicInteger(1);
        Thread commandThread = new Thread(() -> {
            try {
                call(command, s -> {
                    count.incrementAndGet();
                    input.accept(s);
                }, s -> {
                    count.incrementAndGet();
                    error.accept(s);
                });
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }, "command-thread");
        commandThread.start();
        while (count.get() > 0) {
            count.lazySet(0);
            commandThread.join(5000);
        }
        commandThread.interrupt();
    }

    private static void readline(InputStream is, Consumer<String> consumer) throws IOException {
        LineReader reader = new LineReader(new InputStreamReader(is));
        String line;
        for (; ; ) {
            line = reader.readLine();
            if (line == null) {
                break;
            }
            consumer.accept(line);
        }
    }

    public static Path userHome() {
        return Paths.get(System.getProperty("user.home"));
    }

    public static Path userDirectory() {
        return Paths.get(System.getProperty("user.dir"));
    }

    public static Path scriptsDirectory() {
        return userDirectory().resolve("scripts");
    }

    public static String os() {
        return System.getProperty("os.name");
    }

    public static boolean isWindows() {
        return os().startsWith("Windows");
    }

    public static String scriptSuffix() {
        if (isWindows()) {
            return "bat";
        } else {
            return "sh";
        }
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
