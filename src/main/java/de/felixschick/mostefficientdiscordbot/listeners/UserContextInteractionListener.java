package de.felixschick.mostefficientdiscordbot.listeners;

import de.felixschick.mostefficientdiscordbot.MostEfficientDiscordBot;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UserContextInteractionListener extends ListenerAdapter {

    @Override
    public void onUserContextInteraction(final UserContextInteractionEvent event) {
        MostEfficientDiscordBot.getCommandHelper().getUserContextCommand(event.getName()).ifPresent(userContextCommand -> userContextCommand.onInteract(event));
    }
}
