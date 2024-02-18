package de.felixschick.mostefficientdiscordbot.commands.interfaces;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface UserContextCommand {

    public CommandData context();
    public void onInteract(UserContextInteractionEvent event);

}
