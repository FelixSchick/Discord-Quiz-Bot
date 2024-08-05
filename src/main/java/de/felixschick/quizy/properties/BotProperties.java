package de.felixschick.quizy.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

@Component
@Getter
@Setter
public class BotProperties {

    private static final String CONFIG_FILE_PATH = "src/main/resources/bot.properties";
    private Properties properties = new Properties();

    public BotProperties() {
        loadProperties();
    }

    private void loadProperties() {
        try (FileInputStream input = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load properties: " + e.getMessage());
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
        saveProperties();
    }

    private void saveProperties() {
        try (FileOutputStream output = new FileOutputStream(CONFIG_FILE_PATH)) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to save properties: " + e.getMessage());
        }
    }
}