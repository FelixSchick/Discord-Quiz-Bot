package de.felixschick.mostefficientdiscordbot.listeners;

import de.felixschick.mostefficientdiscordbot.MostEfficientDiscordBot;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(final ReadyEvent event) {
        //TODO: check if it is the right place for the methode
        System.out.println("---------Start to registry the commands---------");
        MostEfficientDiscordBot.getCommandHelper().registerGuildCommands(event.getJDA().getGuilds());
        System.out.println("----------End to registry the commands----------");

        System.out.println("PluginStube.net - DiscordBot started succesfully");
        System.out.println("Stats: \n" +
                "total server:" + event.getGuildTotalCount() + "\n" +
                "SQL: " + MostEfficientDiscordBot.getMySQL().isConnected());

    }
}
