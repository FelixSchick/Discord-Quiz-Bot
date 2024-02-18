package de.felixschick.mostefficientdiscordbot.listeners;

import de.felixschick.mostefficientdiscordbot.MostEfficientDiscordBot;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageContextIntegrationListener extends ListenerAdapter {

    @Override
    public void onMessageContextInteraction(final MessageContextInteractionEvent event) {
        MostEfficientDiscordBot.getCommandHelper().getMessageContextCommand(event.getName()).ifPresent(messageContextCommand -> messageContextCommand.onInteraction(event));
    }
}
