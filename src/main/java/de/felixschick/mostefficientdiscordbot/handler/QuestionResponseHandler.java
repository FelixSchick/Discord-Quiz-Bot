package de.felixschick.mostefficientdiscordbot.handler;

import de.felixschick.mostefficientdiscordbot.MostEfficientDiscordBot;
import de.felixschick.mostefficientdiscordbot.helper.MessageHelper;
import de.felixschick.mostefficientdiscordbot.objects.QuizQuestion;
import de.felixschick.mostefficientdiscordbot.objects.QuizQuestionAnswer;
import de.felixschick.mostefficientdiscordbot.utils.QuizProvider;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuestionResponseHandler {

    private QuizProvider quizProvider;

    public QuestionResponseHandler() {
        quizProvider = MostEfficientDiscordBot.getQuizProvider();
    }

    public void handleButtonInteraction(ButtonInteractionEvent event) {
        final String rawButtonID = event.getButton().getId();
        String[] split = rawButtonID.split("%10%");
        if (split.length == 3) {
            final int questionID = Integer.parseInt(split[1]);
            final String answer = split[2].toString().replace("%20%", " ");

            final QuizQuestion quizQuestion = quizProvider.getQuizQuestion(questionID);

            Optional<QuizQuestionAnswer> optionalQuizQuestionAnswer = quizQuestion.getAnswers().stream().filter(quizQuestionAnswer -> quizQuestionAnswer.getAnswer().equals(answer)).findFirst();

            if (optionalQuizQuestionAnswer.isPresent()) {
                QuizQuestionAnswer quizQuestionAnswer = optionalQuizQuestionAnswer.get();

                if (quizQuestionAnswer.isCorrect()) {
                    event.getMessage().reply("The user " + event.getMember().getAsMention() + " got the right answer").queue();

                    List<Button> buttons = new ArrayList<>();

                    event.getMessage().getButtons().forEach(button -> {
                        buttons.add(button.asDisabled());
                    });

                    EmbedBuilder embedBuilder = new EmbedBuilder()
                            .setTitle("Quiz question level: " + quizQuestion.getDifficultyLevel().getDisplayName())
                            .setColor(quizQuestion.getDifficultyLevel().getColor());

                    embedBuilder.addField("Solved", "This question was solved by " + event.getMember().getAsMention(), true);

                    StringBuilder answerBuilder = new StringBuilder();
                    quizQuestion.getAnswers().forEach(questionAnswer ->
                            answerBuilder.append("  -").append(questionAnswer.getAnswer()).append("(").append(questionAnswer.isCorrect()).append(")\n")
                    );
                    embedBuilder.addField("Question", quizQuestion.getQuestion() + "\n" + answerBuilder.toString(), false);



                    event.getMessage().editMessageEmbeds(embedBuilder.build()).setActionRow(buttons).queue();
                }
            } else {
                MessageHelper.error(event.getChannel().asTextChannel(), "No answer to button found!", "there is no answer in the database that fit to the button");
            }
        }
    }

}
