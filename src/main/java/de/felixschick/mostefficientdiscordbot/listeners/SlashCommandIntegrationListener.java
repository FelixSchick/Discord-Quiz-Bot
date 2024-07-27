package de.felixschick.mostefficientdiscordbot.listeners;

import de.felixschick.mostefficientdiscordbot.MostEfficientDiscordBot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommandIntegrationListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(final SlashCommandInteractionEvent event) {
        MostEfficientDiscordBot.getCommandHelper().getSlashCommand(event.getName()).ifPresent(slashCommand -> slashCommand.onIntegration(event));
    }
}
