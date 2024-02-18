package de.felixschick.mostefficientdiscordbot.commands;

import de.felixschick.mostefficientdiscordbot.commands.interfaces.UserContextCommand;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;


public class TestUserContextCommand implements UserContextCommand {
    @Override
    public CommandData context() {
        return Commands.user("test user context menu");
    }

    @Override
    public void onInteract(UserContextInteractionEvent event) {
        event.reply("Test Message Here").queue();
    }
}
