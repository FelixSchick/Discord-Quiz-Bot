package de.felixschick.mostefficientdiscordbot.helper;

import de.felixschick.mostefficientdiscordbot.commands.interfaces.MessageContextCommand;
import de.felixschick.mostefficientdiscordbot.commands.interfaces.SlashCommand;
import de.felixschick.mostefficientdiscordbot.commands.interfaces.UserContextCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CommandHelper {

    public List<SlashCommand> slashCommandList;
    public List<UserContextCommand> userContextCommandList;
    public List<MessageContextCommand> messageContextCommandList;

    public CommandHelper() {
        initSlashCommands();
        initUserContextCommands();
        initMessageContextCommands();
    }

    public Optional<SlashCommand> getSlashCommand(final String name) {
        return slashCommandList.stream().filter(slashCommand -> slashCommand.command().getName().equalsIgnoreCase(name)).findFirst();
    }

    public Optional<UserContextCommand> getUserContextCommand(final String name) {
        return userContextCommandList.stream().filter(userContextCommand -> userContextCommand.context().getName().equalsIgnoreCase(name)).findFirst();
    }

    public Optional<MessageContextCommand> getMessageContextCommand(final String name) {
        return messageContextCommandList.stream().filter(messageContextCommand -> messageContextCommand.context().getName().equalsIgnoreCase(name)).findFirst();
    }

    public void registerGuildCommands(final List<Guild> guilds) {
        for (final Guild guild : guilds) {
            guild.updateCommands().addCommands(getCommandDataCollection()).queue();
            System.out.println("Command(s) added to " + guild.getName());
        }
    }

    public void registerGlobalCommands() {
        //TODO: implement global commands (if needed)
    }

    private Collection<CommandData> getCommandDataCollection() {
        final Collection<CommandData> dataCollection = new ArrayList<>();

        for (final SlashCommand slashCommand : slashCommandList) {
            final SlashCommandData data = slashCommand.command();
            dataCollection.add(data);
        }

        for (final MessageContextCommand messageContextCommand : messageContextCommandList){
            CommandData commandData = messageContextCommand.context();
            dataCollection.add(commandData);
        }

        for (final UserContextCommand userContextCommand : userContextCommandList){
            final CommandData commandData = userContextCommand.context();
            dataCollection.add(commandData);
        }

        return dataCollection;
    }

    private void initSlashCommands() {
        slashCommandList = new ArrayList<>();

        final Reflections reflections = new Reflections("de.felixschick.mostefficientdiscordbot.commands");

        reflections.getSubTypesOf(SlashCommand.class).forEach(aClass -> {
            try {
                slashCommandList.add(aClass.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        System.out.println(slashCommandList.toString());
    }

    private void initUserContextCommands() {
        userContextCommandList = new ArrayList<>();

        Reflections reflections = new Reflections("de.felixschick.mostefficientdiscordbot.commands");

        reflections.getSubTypesOf(UserContextCommand.class).forEach(aClass -> {
            try {
                userContextCommandList.add(aClass.newInstance());
            }  catch (Exception e) {
                e.printStackTrace();
            }
        });

        System.out.println(userContextCommandList.toString());
    }

    private void initMessageContextCommands() {
        messageContextCommandList = new ArrayList<>();

        Reflections reflections = new Reflections("de.felixschick.mostefficientdiscordbot.commands");

        reflections.getSubTypesOf(MessageContextCommand.class).forEach(aClass -> {
            try {
                messageContextCommandList.add(aClass.newInstance());
            }  catch (Exception e) {
                e.printStackTrace();
            }
        });

        System.out.println(messageContextCommandList.toString());
    }
}
