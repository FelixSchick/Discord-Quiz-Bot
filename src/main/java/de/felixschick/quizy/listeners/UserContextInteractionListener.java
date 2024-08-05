package de.felixschick.quizy.listeners;

import de.felixschick.quizy.QuizyApplication;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UserContextInteractionListener extends ListenerAdapter {

    @Override
    public void onUserContextInteraction(final UserContextInteractionEvent event) {
        QuizyApplication.getCommandHelper().getUserContextCommand(event.getName()).ifPresent(userContextCommand -> userContextCommand.onInteract(event));
    }
}
