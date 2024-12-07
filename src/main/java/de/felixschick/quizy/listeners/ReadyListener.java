package de.felixschick.quizy.listeners;

import de.felixschick.quizy.QuizyApplication;
import de.felixschick.quizy.helper.CommandHelper;
import de.felixschick.quizy.sql.MySQL;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class ReadyListener extends ListenerAdapter {


    private CommandHelper commandHelper;

    private MySQL mySQL;

    @Autowired
    public ReadyListener(CommandHelper commandHelper, MySQL mySQL) {
        this.commandHelper = commandHelper;
        this.mySQL = mySQL;
    }

    @Override
    public void onReady(final ReadyEvent event) {
        System.out.println("---------Start to registry the commands---------");
        System.out.println("guild only:");
        commandHelper.registerGuildCommands(List.of(event.getJDA().getGuildById("947062980565151774")));
        System.out.println("global:");
        commandHelper.registerGlobalCommands();
        System.out.println("----------End to registry the commands----------");

        System.out.println("Quizy - by Felix Schick started succesfully");
        System.out.println("Stats: \n" +
                "total server:" + event.getGuildTotalCount() + "\n" +
                "SQL: " + mySQL.isConnected());

    }
}
