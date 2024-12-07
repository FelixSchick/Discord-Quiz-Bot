package de.felixschick.quizy.sql;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class MySQL {

    @Getter
    private Connection connection;
    private final DatabaseProperties databaseProperties;

    @Autowired
    public MySQL(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
        connect();
        createTables();
    }

    private void connect() {
        if (!isConnected()) {
            try {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://" + databaseProperties.getHost() + ":" + databaseProperties.getPort() + "/" + databaseProperties.getDatabase() + "?autoReconnect=true",
                        databaseProperties.getUsername(),
                        databaseProperties.getPassword()
                );
                System.out.println("Connection to MySQL database established.");
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to connect to the database", e);
            }
        }
    }

    public void disconnect() {
        try {
            if (isConnected()) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        if (this.connection != null) {
            try {
                return !this.connection.isClosed() && this.connection.isValid(10);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public int update(String qry) {
        if (!isConnected()) {
            connect();
        }

        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(qry);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public ResultSet query(String query) {
        if (!isConnected()) {
            connect();
        }

        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            connect(); // Attempt to reconnect
            return null;
        }
    }

    private void createTables() {
        if (!isConnected()) return;
        update("CREATE TABLE IF NOT EXISTS bot_info (id VARCHAR(40), value TEXT, PRIMARY KEY (id))");
        update("CREATE TABLE IF NOT EXISTS quiz_questions (id INT AUTO_INCREMENT, question TEXT, guildid VARCHAR(20), difficultylevel VARCHAR(20), answers TEXT, PRIMARY KEY (id))");
    }
}
