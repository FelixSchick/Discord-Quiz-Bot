package de.felixschick.quizy.listeners;

import de.felixschick.quizy.QuizyApplication;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(final ReadyEvent event) {
        System.out.println("---------Start to registry the commands---------");
        /*.out.println("guild only:");
        QuizyApplication.getCommandHelper().registerGuildCommands(event.getJDA().getGuilds());*/
        System.out.println("global:");
        QuizyApplication.getCommandHelper().registerGlobalCommands();
        System.out.println("----------End to registry the commands----------");

        System.out.println("Quizy - by Felix Schick started succesfully");
        System.out.println("Stats: \n" +
                "total server:" + event.getGuildTotalCount() + "\n" +
                "SQL: " + QuizyApplication.getMySQL().isConnected());

    }
}
