package de.felixschick.quizy.handler;

import de.felixschick.quizy.QuizyApplication;
import de.felixschick.quizy.enums.QuizQuestionDifficultyLevel;
import de.felixschick.quizy.objects.QuizQuestion;
import de.felixschick.quizy.objects.QuizQuestionAnswer;
import de.felixschick.quizy.utils.QuizProvider;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.List;

public class QuestionCreationHandler {

    private final QuizProvider quizProvider;

    public QuestionCreationHandler() {
        quizProvider = QuizyApplication.getQuizProvider();
    }

    public void handleCreationSubCommand(SlashCommandInteractionEvent event) {
        if (event.getSubcommandName().equalsIgnoreCase("question")) {

            final String questions = event.getOption("question").getAsString();
            final QuizQuestionDifficultyLevel difficultyLevel = QuizQuestionDifficultyLevel.valueOf(event.getOption("difficultylevel").getAsString());

            quizProvider.creatQuizQuestion(event.getGuild().getIdLong(), questions, difficultyLevel, new ArrayList<>()).ifPresentOrElse(quizQuestion -> {
                event.reply("Quiz-Frage: " + quizQuestion.getQuestion() + " wurde erfolgreich mit der ID #" + quizQuestion.getId() + " erstellt")
                        .setEphemeral(true).queue();
            }, () -> event.reply("Quiz-Frage: " + questions + " wurde erfolgreich erstellt")
                    .setEphemeral(true).queue());
        } else if (event.getSubcommandName().equalsIgnoreCase("answer")) {
            final int id = event.getOption("id").getAsInt();
            final String answer = event.getOption("answer").getAsString();
            final boolean isCorrect = event.getOption("correct").getAsBoolean();

            final QuizQuestion quizQuestion = quizProvider.getQuizQuestion(id);
            List<QuizQuestionAnswer> answers = quizQuestion.getAnswers();

            QuizQuestionAnswer newQuizQuestionAnswer = new QuizQuestionAnswer(answer, isCorrect);
            answers.add(newQuizQuestionAnswer);

            event.reply("ID:" + id + " zu dieser Frage wurde die Antwort: " + newQuizQuestionAnswer.getAnswer() + "(" + newQuizQuestionAnswer.isCorrect() + ") hinzugefügt.").setEphemeral(true).queue();
        }
    }

}
