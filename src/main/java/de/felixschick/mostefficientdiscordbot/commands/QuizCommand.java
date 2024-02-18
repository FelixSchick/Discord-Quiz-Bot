package de.felixschick.mostefficientdiscordbot.commands;

import de.felixschick.mostefficientdiscordbot.MostEfficientDiscordBot;
import de.felixschick.mostefficientdiscordbot.commands.interfaces.SlashCommand;
import de.felixschick.mostefficientdiscordbot.enums.QuizQuestionDifficultyLevel;
import de.felixschick.mostefficientdiscordbot.objects.QuizQuestion;
import de.felixschick.mostefficientdiscordbot.utils.QuizProvider;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.util.Arrays;

public class QuizCommand implements SlashCommand {

    private QuizProvider quizProvider;

    public QuizCommand() {
        quizProvider = MostEfficientDiscordBot.getQuizProvider();
    }

    @Override
    public SlashCommandData command() {
        SlashCommandData slash = Commands.slash("quiz", "quiz command");

        OptionData questionOptionData = new OptionData(OptionType.STRING, "difficultylevel", "Set the difficulty of the question", true);
        Arrays.stream(QuizQuestionDifficultyLevel.values()).forEach(quizQuestionDifficultyLevel -> {
            questionOptionData.addChoice(quizQuestionDifficultyLevel.getDisplayName(), quizQuestionDifficultyLevel.name());
        });

        slash.addSubcommands(
                new SubcommandData("question", "get a question")
                        .addOptions(questionOptionData),
                new SubcommandData("list", "list every question")
        );

        slash.addSubcommandGroups(
                new SubcommandGroupData("add", "add a new question/answer")
                        .addSubcommands(new SubcommandData("question", "add a question")
                                .addOption(OptionType.STRING, "question", "the question", true)
                                .addOptions(questionOptionData)
                        )
                        .addSubcommands(new SubcommandData("answer", "add a answer to a question")
                                .addOption(OptionType.INTEGER, "id", "id of the question", true)
                                .addOption(OptionType.STRING, "answer", "the answer", true)
                                .addOption(OptionType.BOOLEAN, "correct", "is the anwer right or wrong?", true)
                        )
        );



        return slash;
    }

    @Override
    public void onIntegration(SlashCommandInteractionEvent event) {
        if (event.getSubcommandGroup() != null || event.getSubcommandGroup() != "") {
            switch (event.getSubcommandGroup()) {
                case "add":
                    event.reply("test").setEphemeral(true).queue();
            }
        } else if (event.getSubcommandName() != null || event.getSubcommandName() != "") {
            event.reply("test1").setEphemeral(true).queue();
        }
    }
}
