package com.backend.config;

import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads a local ".env" file (KEY=VALUE per line, "#" comments, optional
 * surrounding quotes) into the Spring Environment as a low-priority property
 * source, so secrets like GEMINI_API_KEY can be kept out of application.properties
 * and out of version control (see .gitignore) while still resolving
 * placeholders such as {@code ${GEMINI_API_KEY:}}.
 *
 * Looks for ".env" in the current working directory first, then the parent
 * directory (covers running "mvnw spring-boot:run" from backend/ while the
 * .env file lives at the repository root). Silently does nothing if no file
 * is found - real environment variables always take precedence over this.
 */
public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        for (Path candidate : List.of(Path.of(".env"), Path.of("..", ".env"))) {
            if (Files.isRegularFile(candidate)) {
                Map<String, Object> values = parse(candidate);
                if (!values.isEmpty()) {
                    environment.getPropertySources().addLast(new MapPropertySource("dotenv", values));
                    System.out.println("[dotenv] Loaded " + values.size() + " propert"
                            + (values.size() == 1 ? "y" : "ies") + " from " + candidate.toAbsolutePath().normalize()
                            + " (keys: " + values.keySet() + ")");
                }
                return;
            }
        }
    }

    private Map<String, Object> parse(Path file) {
        Map<String, Object> values = new LinkedHashMap<>();
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                int eq = trimmed.indexOf('=');
                if (eq <= 0) {
                    continue;
                }
                String key = trimmed.substring(0, eq).trim();
                String value = trimmed.substring(eq + 1).trim();
                if (value.length() >= 2 && (value.startsWith("\"") && value.endsWith("\"")
                        || value.startsWith("'") && value.endsWith("'"))) {
                    value = value.substring(1, value.length() - 1);
                }
                values.put(key, value);
            }
        } catch (IOException ex) {
            // Ignore unreadable .env - fall back to real environment variables only.
        }
        return values;
    }
}
