package de.felixschick.quizy.controllers;

import de.felixschick.quizy.DTO.StatsResponse;
import de.felixschick.quizy.QuizyApplication;
import de.felixschick.quizy.sql.MySQL;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SystemController {

    @Autowired
    private MySQL mySQL;

    /**
     * a methode that returns the current bot status
     * @return online when the bot is online and nothing if this is offline
     */
    @GetMapping("/status")
    public String getStatus() {
        return "online";
    }

    @GetMapping("/stats")
    public StatsResponse getStats() {

        return new StatsResponse(
                QuizyApplication.getJda().getGuilds().size(),
                mySQL.isConnected()
        );
    }

    @GetMapping("/info/guild/{id}")
    public String getGuildInfo(@PathVariable final long guildID) {
        Guild targetGuild;
        try {
            targetGuild = QuizyApplication.getJda().getGuilds().stream().filter(guild -> guild.getIdLong() == guildID).findFirst().orElseThrow();
        } catch (Exception e) {
            return "the guild with the id " + guildID + " can not be found";
        }

        return targetGuild.toString();

    }
}
