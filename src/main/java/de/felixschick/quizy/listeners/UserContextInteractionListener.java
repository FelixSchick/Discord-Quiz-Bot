package de.felixschick.quizy.listeners;

import de.felixschick.quizy.QuizyApplication;
import de.felixschick.quizy.helper.CommandHelper;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserContextInteractionListener extends ListenerAdapter {

    @Autowired
    private CommandHelper commandHelper;

    @Override
    public void onUserContextInteraction(final UserContextInteractionEvent event) {
        commandHelper.getUserContextCommand(event.getName()).ifPresent(userContextCommand -> userContextCommand.onInteract(event));
    }
}
