package de.felixschick.quizy.utils;

import de.felixschick.quizy.QuizyApplication;
import de.felixschick.quizy.objects.QuizQuestion;
import de.felixschick.quizy.sql.MySQL;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class BotInformationProvider {
    private MySQL mySQL;

    public BotInformationProvider() {
        this.mySQL = QuizyApplication.getMySQL();

        if (!existsInSQL("activity_type").get()) {
            setInfo("activity_type", "COMPETING");
        }

        if (!existsInSQL("activity_label").get()) {
            setInfo("activity_label", "Quiz");
        }

        System.out.println(getInfo("activity_type"));
    }

    public void setInfo(final String id, final String value) {
        if (!existsInSQL(id).get()) {
            CompletableFuture.supplyAsync(() ->
                    mySQL.update("INSERT INTO `bot_info`(`id`, `value`) VALUES ('"+id+"', '"+ value +"');")
            );
        } else {
            mySQL.update("UPDATE `bot_info` SET `value` = '" + value + "' WHERE `id` = '" + id + "';");
        }
    }

    public String getInfo(final String id) {
        try {
            final ResultSet resultSet = mySQL.qry("SELECT * FROM `bot_info` WHERE `id` = '" + id + "'");
            if (resultSet.next()) {
                return resultSet.getString("value");
            } else
                return null;

        }  catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Boolean> existsInSQL(final String id) {
        try {
            return (Optional<Boolean>) CompletableFuture.supplyAsync(() -> {
                final ResultSet resultSet = mySQL.qry("SELECT * FROM `bot_info` WHERE `id`='" + id + "'");
                try {
                    if (resultSet.next()) {
                        return Optional.of(true);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return Optional.of(false);
            }).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
