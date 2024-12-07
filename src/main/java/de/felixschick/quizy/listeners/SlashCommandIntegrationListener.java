package de.felixschick.quizy.listeners;

import de.felixschick.quizy.QuizyApplication;
import de.felixschick.quizy.helper.CommandHelper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SlashCommandIntegrationListener extends ListenerAdapter {

    @Autowired
    private CommandHelper commandHelper;
    @Override
    public void onSlashCommandInteraction(final SlashCommandInteractionEvent event) {
        commandHelper.getSlashCommand(event.getName()).ifPresent(slashCommand -> slashCommand.onIntegration(event));
    }
}
