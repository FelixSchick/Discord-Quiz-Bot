package de.felixschick.mostefficientdiscordbot.listeners;

import de.felixschick.mostefficientdiscordbot.MostEfficientDiscordBot;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(final ReadyEvent event) {
        System.out.println("---------Start to registry the commands---------");
        System.out.println("guild only:");
        //MostEfficientDiscordBot.getCommandHelper().registerGuildCommands(event.getJDA().getGuilds());
        System.out.println("global:");
        MostEfficientDiscordBot.getCommandHelper().registerGlobalCommands();
        System.out.println("----------End to registry the commands----------");

        System.out.println("Quizy - by Felix Schick started succesfully");
        System.out.println("Stats: \n" +
                "total server:" + event.getGuildTotalCount() + "\n" +
                "SQL: " + MostEfficientDiscordBot.getMySQL().isConnected());

    }
}
