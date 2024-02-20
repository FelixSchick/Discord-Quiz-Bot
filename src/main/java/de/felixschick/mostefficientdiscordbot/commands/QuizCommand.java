package de.felixschick.mostefficientdiscordbot.commands;

import de.felixschick.mostefficientdiscordbot.MostEfficientDiscordBot;
import de.felixschick.mostefficientdiscordbot.commands.interfaces.SlashCommand;
import de.felixschick.mostefficientdiscordbot.enums.QuizQuestionDifficultyLevel;
import de.felixschick.mostefficientdiscordbot.objects.QuizQuestionAnswer;
import de.felixschick.mostefficientdiscordbot.utils.QuizProvider;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            if (event.getSubcommandGroup().equalsIgnoreCase("add")) {
                if (event.getSubcommandName().equalsIgnoreCase("question")) {
                    String questions = event.getOption("question").getAsString();
                    QuizQuestionDifficultyLevel difficultyLevel = QuizQuestionDifficultyLevel.valueOf(event.getOption("difficultylevel").getAsString());

                    quizProvider.creatQuizQuestion(questions, difficultyLevel, new ArrayList<>()).ifPresentOrElse(quizQuestion -> {
                        event.reply("Quiz-Frage: " + quizQuestion.getQuestion() + " wurde erfolgreich mit der ID #" + quizQuestion.getId() + " erstellt")
                                .setEphemeral(true).queue();
                    }, () -> {
                        event.reply("Quiz-Frage: " + questions + " wurde erfolgreich erstellt")
                                .setEphemeral(true).queue();
                    });
                } else if (event.getSubcommandName().equalsIgnoreCase("answer")) {
                    int id = event.getOption("id").getAsInt();
                    String answer = event.getOption("answer").getAsString();
                    boolean isCorrect = event.getOption("correct").getAsBoolean();

                    List<QuizQuestionAnswer> answers = quizProvider.getQuizQuestion(id).getAnswers();

                    QuizQuestionAnswer newQuizQuestionAnswer = new QuizQuestionAnswer(answer, isCorrect);
                    answers.add(newQuizQuestionAnswer);

                    event.reply("ID:" + id + " zu dieser Frage wurde die Antwort: " + newQuizQuestionAnswer.getAnswer() + "(" + newQuizQuestionAnswer.isCorrect() + ") hinzugefügt.").setEphemeral(true).queue();
                }
            }
        } else if (event.getSubcommandName() != null || event.getSubcommandName() != "") {

            event.reply("test1").setEphemeral(true).queue();
        }
    }
}
