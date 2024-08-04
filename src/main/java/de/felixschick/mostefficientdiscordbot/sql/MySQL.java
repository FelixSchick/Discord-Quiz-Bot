package de.felixschick.mostefficientdiscordbot.sql;

import de.felixschick.mostefficientdiscordbot.MostEfficientDiscordBot;
import lombok.Getter;

import java.sql.*;

public class MySQL {

    @Getter
    private Connection connection;
    private String host;
    private String port;
    private String database;
    private String username;
    private String password;

    public MySQL(String host, String port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;

        //init
        this.connect();
        this.createTabels();
    }

    public MySQL(String host, String port, String database, String username) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = "";

        //init
        this.connect();
        this.createTabels();
    }

    public void connect() {
        if (!isConnected()) {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", username, password);

                //connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database , username, password);
                System.out.println("§7[§bMySQL§7] §aDie verbindung zur MySQL-Datenbank wurde hergestellt.");
            } catch (SQLException exception) {
                exception.printStackTrace();
                //System.out.println("§7[§bMySQL§7] §cEin Fehler ist aufgetreten beim verbinden, bitte überprüfe deine config!");
            }
        }
    }

    public void disconnect() {
        try {
            if (isConnected()) connection.close();
        } catch (SQLException exception) {
            exception.fillInStackTrace();
            System.out.println("§7[§bMySQL§7] §cEin Fehler ist aufgetreten, bitte überprüfe deine config!");
        }
    }

    public boolean isConnected() {
        if (this.connection != null) {
            try {
                if (!this.connection.isClosed()) {
                    if (this.connection.isValid(10)) {
                        return true;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public int update(String qry) {
        if (!isConnected()) {
            disconnect();
            connect();
        }

        try {
            Statement statement = connection.createStatement();
            int value = statement.executeUpdate(qry);
            statement.close();
            return value;
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.out.println("§7[§bMySQL§7] §cEin Fehler ist aufgetreten, Exeption: update error");
        }
        return -1;
    }

    public ResultSet qry(String query) {
        if (!isConnected()) {
            disconnect();
            connect();
        }

        ResultSet rs = null;
        try {
            Statement st = connection.createStatement();
            rs = st.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            connect();
        }
        return rs;
    }

    public void createTabels() {
        //quiz-questions: guildid, id, question, difficultylevel, answers(answer, true; answer, false, ...);
        if (!isConnected()) return;
        update("CREATE TABLE IF NOT EXISTS quiz_questions (id int auto_increment, question TEXT, guildid VARCHAR(20), difficultylevel VARCHAR(20), answers TEXT, PRIMARY KEY (id))");
    }
}
