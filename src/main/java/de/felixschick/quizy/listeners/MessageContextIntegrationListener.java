package de.felixschick.quizy.listeners;

import de.felixschick.quizy.QuizyApplication;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageContextIntegrationListener extends ListenerAdapter {

    @Override
    public void onMessageContextInteraction(final MessageContextInteractionEvent event) {
        QuizyApplication.getCommandHelper().getMessageContextCommand(event.getName()).ifPresent(messageContextCommand -> messageContextCommand.onInteraction(event));
    }
}
