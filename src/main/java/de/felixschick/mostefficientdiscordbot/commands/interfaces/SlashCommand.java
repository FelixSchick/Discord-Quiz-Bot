package de.felixschick.mostefficientdiscordbot.commands.interfaces;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface SlashCommand {

    public SlashCommandData command();
    public void onIntegration(SlashCommandInteractionEvent event);
    
}
