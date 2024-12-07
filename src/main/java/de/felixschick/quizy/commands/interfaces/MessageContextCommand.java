package de.felixschick.quizy.commands.interfaces;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface MessageContextCommand {

    public CommandData context();
    public void onInteraction(MessageContextInteractionEvent event);

}
