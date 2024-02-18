package de.felixschick.mostefficientdiscordbot.commands;

import de.felixschick.mostefficientdiscordbot.MostEfficientDiscordBot;
import de.felixschick.mostefficientdiscordbot.commands.interfaces.SlashCommand;
import de.felixschick.mostefficientdiscordbot.enums.QuizQuestionDifficultyLevel;
import de.felixschick.mostefficientdiscordbot.objects.QuizQuestion;
import de.felixschick.mostefficientdiscordbot.objects.QuizQuestionAnswer;
import de.felixschick.mostefficientdiscordbot.utils.QuizProvider;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.ArrayList;
import java.util.Optional;


public class TestCommand implements SlashCommand {

    private QuizProvider quizProvider;

    public TestCommand() {
        quizProvider = MostEfficientDiscordBot.getQuizProvider();
    }

    @Override
    public SlashCommandData command() {
        return Commands.slash("test", "Test");
    }

    @Override
    public void onIntegration(SlashCommandInteractionEvent event) {
        quizProvider.getQuizQuestion(1).setQuestion("Test+1");
        event.reply(quizProvider.getQuizQuestion(1).getQuestion()).setEphemeral(true).queue();
    }
}
