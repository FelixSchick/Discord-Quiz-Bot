package de.felixschick.quizy.listeners;

import de.felixschick.quizy.QuizyApplication;
import de.felixschick.quizy.helper.CommandHelper;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageContextIntegrationListener extends ListenerAdapter {

    @Autowired
    private CommandHelper commandHelper;

    @Override
    public void onMessageContextInteraction(final MessageContextInteractionEvent event) {
        commandHelper.getMessageContextCommand(event.getName()).ifPresent(messageContextCommand -> messageContextCommand.onInteraction(event));
    }
}
