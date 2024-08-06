package de.felixschick.quizy.utils;

import de.felixschick.quizy.sql.MySQL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class BotInformationProvider {

    private final MySQL mySQL;

    @Autowired
    public BotInformationProvider(MySQL mySQL) {
        this.mySQL = mySQL;

        initializeDefaultValues();
    }

    private void initializeDefaultValues() {
        if (!existsInSQL("activity_type")) {
            setInfo("activity_type", "COMPETING");
        }

        if (!existsInSQL("activity_label")) {
            setInfo("activity_label", "Quiz");
        }

        log.info("Activity type: {}", getInfo("activity_type"));
    }

    public void setInfo(final String id, final String value) {
        if (!existsInSQL(id)) {
            CompletableFuture.runAsync(() ->
                    mySQL.update("INSERT INTO bot_info (id, value) VALUES ('" + id + "', '" + value + "');")
            );
        } else {
            mySQL.update("UPDATE bot_info SET value = '" + value + "' WHERE id = '" + id + "';");
        }
    }

    public String getInfo(final String id) {
        try (ResultSet resultSet = mySQL.query("SELECT * FROM bot_info WHERE id = '" + id + "'")) {
            if (resultSet.next()) {
                return resultSet.getString("value");
            } else {
                return null;
            }
        } catch (SQLException e) {
            log.error("Error retrieving info from database: {}", e.getMessage());
            return null;
        }
    }

    private Boolean existsInSQL(final String id) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try (ResultSet resultSet = mySQL.query("SELECT * FROM bot_info WHERE id = '" + id + "'")) {
                    return resultSet.next();
                } catch (SQLException e) {
                    log.error("Error checking existence in database: {}", e.getMessage());
                    return false;
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error executing async task: {}", e.getMessage());
            return false;
        }
    }
}
