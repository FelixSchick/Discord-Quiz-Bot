package de.felixschick.quizy.sql;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "db")
public class DatabaseProperties {

    // Getters and setters
    private String host;
    private String port;
    private String database;
    private String username;
    private String password;

    public void setHost(String host) { this.host = host; }

    public void setPort(String port) { this.port = port; }

    public void setDatabase(String database) { this.database = database; }

    public void setUsername(String username) { this.username = username; }

    public void setPassword(String password) { this.password = password; }
}