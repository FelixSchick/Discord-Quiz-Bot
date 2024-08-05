package de.felixschick.quizy.controllers;


import de.felixschick.quizy.DTO.ActivityRequest;
import de.felixschick.quizy.QuizyApplication;
import de.felixschick.quizy.properties.BotProperties;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bot-info")
public class BotInfoController {

    @Autowired
    private BotProperties botProperties;


    @PostMapping("/activity")
    public String setActivity(@RequestBody ActivityRequest request) {
        Activity.ActivityType activityType;

        try {
            activityType = Activity.ActivityType.valueOf(request.getActivityType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid activity type: " + request.getActivityType());
        }

        botProperties.setProperty("activity-type", request.getActivityType());
        botProperties.setProperty("activity-label",request.getActivityLabel());

        QuizyApplication.getJda().getPresence().setActivity(Activity.of(activityType, request.getActivityLabel()));
        return "success";
    }
}
