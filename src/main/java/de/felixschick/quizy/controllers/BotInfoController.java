package de.felixschick.quizy.controllers;


import de.felixschick.quizy.DTO.ActivityRequest;
import de.felixschick.quizy.QuizyApplication;
import de.felixschick.quizy.utils.BotInformationProvider;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.web.bind.annotation.*;

import javax.management.ConstructorParameters;

@RestController
@RequestMapping("/api/bot-info")
public class BotInfoController {

    /**
     * Set the presence of the bot and save it
     * @param request
     * @return success string
     */
    @PostMapping("/activity")
    public String setActivity(@RequestBody ActivityRequest request) {
        Activity.ActivityType activityType;

        try {
            activityType = Activity.ActivityType.valueOf(request.getActivityType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid activity type: " + request.getActivityType());
        }

        QuizyApplication.getBotInformationProvider().setInfo("activity_type", request.getActivityType());
        QuizyApplication.getBotInformationProvider().setInfo("activity_label", request.getActivityLabel());

        QuizyApplication.getJda().getPresence().setActivity(Activity.of(activityType, request.getActivityLabel()));
        return "success";
    }

    /**
     * Get the current activity
     * @return the current activity
     */
    @GetMapping("/activity")
    public ActivityRequest getActivity() {
        return new ActivityRequest(
                QuizyApplication.getBotInformationProvider().getInfo("activity_type"),
                QuizyApplication.getBotInformationProvider().getInfo("activity_label")
        );
    }
}
