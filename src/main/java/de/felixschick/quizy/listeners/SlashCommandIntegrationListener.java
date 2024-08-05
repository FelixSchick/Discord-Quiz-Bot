package de.felixschick.quizy.listeners;

import de.felixschick.quizy.QuizyApplication;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommandIntegrationListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(final SlashCommandInteractionEvent event) {
        QuizyApplication.getCommandHelper().getSlashCommand(event.getName()).ifPresent(slashCommand -> slashCommand.onIntegration(event));
    }
}
