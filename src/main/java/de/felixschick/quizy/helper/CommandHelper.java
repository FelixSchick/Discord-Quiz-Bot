package de.felixschick.quizy.helper;

import de.felixschick.quizy.QuizyApplication;
import de.felixschick.quizy.commands.interfaces.MessageContextCommand;
import de.felixschick.quizy.commands.interfaces.SlashCommand;
import de.felixschick.quizy.commands.interfaces.UserContextCommand;
import de.felixschick.quizy.utils.ApplicationContextProvider;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class CommandHelper {

    @Autowired
    private ApplicationContext applicationContext;

    public List<SlashCommand> slashCommandList;
    public List<UserContextCommand> userContextCommandList;
    public List<MessageContextCommand> messageContextCommandList;

    @PostConstruct
    public void init() {
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
        QuizyApplication.getJda().updateCommands().addCommands(getCommandDataCollection()).queue();
        System.out.println("Commands added globally");
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

        final Reflections reflections = new Reflections("de.felixschick.quizy.commands");

        reflections.getSubTypesOf(SlashCommand.class).forEach(aClass -> {
            try {
                SlashCommand command = applicationContext.getBean(aClass);
                slashCommandList.add(command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        System.out.println(slashCommandList.toString());
    }

    private void initUserContextCommands() {
        userContextCommandList = new ArrayList<>();

        Reflections reflections = new Reflections("de.felixschick.quizy.commands");

        reflections.getSubTypesOf(UserContextCommand.class).forEach(aClass -> {
            try {
                UserContextCommand userContextCommand = ApplicationContextProvider.getContext().getBean(aClass);
                userContextCommandList.add(userContextCommand);
            }  catch (Exception e) {
                e.printStackTrace();
            }
        });

        System.out.println(userContextCommandList.toString());
    }

    private void initMessageContextCommands() {
        messageContextCommandList = new ArrayList<>();

        Reflections reflections = new Reflections("de.felixschick.quizy.commands");

        reflections.getSubTypesOf(MessageContextCommand.class).forEach(aClass -> {
            try {
                MessageContextCommand messageContextCommand = ApplicationContextProvider.getContext().getBean(aClass);
                messageContextCommandList.add(messageContextCommand);
            }  catch (Exception e) {
                e.printStackTrace();
            }
        });

        System.out.println(messageContextCommandList.toString());
    }
}
