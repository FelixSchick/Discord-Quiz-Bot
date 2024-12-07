package de.felixschick.quizy.commands;

import de.felixschick.quizy.QuizyApplication;
import de.felixschick.quizy.commands.interfaces.SlashCommand;
import de.felixschick.quizy.enums.QuizQuestionDifficultyLevel;
import de.felixschick.quizy.handler.QuestionCreationHandler;
import de.felixschick.quizy.objects.QuizQuestion;
import de.felixschick.quizy.utils.QuizProvider;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class QuizCommand implements SlashCommand {


    private QuizProvider quizProvider;


    private QuestionCreationHandler creationHandler;

    private Random random;

    @Autowired
    public QuizCommand(QuizProvider quizProvider, QuestionCreationHandler creationHandler) {
        this.quizProvider = quizProvider;
        this.creationHandler = creationHandler;

        random = new Random();
    }

    @Override
    public SlashCommandData command() {
        SlashCommandData slash = Commands.slash("quiz", "quiz command");

        OptionData questionOptionData = new OptionData(OptionType.STRING, "difficultylevel", "Set the difficulty of the question", true);
        Arrays.stream(QuizQuestionDifficultyLevel.values()).forEach(quizQuestionDifficultyLevel -> questionOptionData.addChoice(quizQuestionDifficultyLevel.getDisplayName(), quizQuestionDifficultyLevel.name()));

        slash.addSubcommands(
                new SubcommandData("question", "get a question")
                        .addOptions(questionOptionData),
                new SubcommandData("list", "list every question")
        );

        slash.addSubcommandGroups(
                new SubcommandGroupData("create", "create a new question/answer")
                        .addSubcommands(new SubcommandData("question", "create a question")
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
        if (event.getSubcommandGroup() != null && event.getSubcommandGroup() != "") {
            if (event.getSubcommandGroup().equalsIgnoreCase("create") && (event.getSubcommandName() != null)) {
                creationHandler.handleCreationSubCommand(event);
            }
        } else if (event.getSubcommandName() != null && event.getSubcommandName() != "") {
            if (event.getSubcommandName().equalsIgnoreCase("list")) {
                sendQuestionListEmbed(event);

            } else if (event.getSubcommandName().equalsIgnoreCase("question")) {
                final QuizQuestionDifficultyLevel difficultyLevel = QuizQuestionDifficultyLevel.valueOf(event.getOption("difficultylevel").getAsString());
                final List<QuizQuestion> quizQuestions = quizProvider.getQuizQuestions(event.getGuild().getIdLong(), difficultyLevel);
                final QuizQuestion randomQuestion = quizQuestions.get(random.nextInt(quizQuestions.size()));

                sendQuestionEmbed(event, difficultyLevel, randomQuestion);
            }
        }
    }



    private void sendQuestionListEmbed(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Quiz questions")
                .setColor(Color.BLUE);

        for (QuizQuestion quizQuestion : quizProvider.getAllQuizQuestions()) {
            if (quizQuestion.getGuildID() == Objects.requireNonNull(event.getGuild()).getIdLong()
                    || quizQuestion.getGuildID() == -1) {

                StringBuilder answerBuilder = new StringBuilder();
                quizQuestion.getAnswers().forEach(quizQuestionAnswer ->
                        answerBuilder.append("  -").append(quizQuestionAnswer.getAnswer()).append("\n")
                );
                embedBuilder.addField("Question " + quizQuestion.getId(), quizQuestion.getQuestion() + "\n" + answerBuilder.toString(), false);

            }

        }

        embedBuilder.setFooter(Calendar.getInstance().getTime().toString());

        event.replyEmbeds(embedBuilder.build()).queue();
    }

    private void sendQuestionEmbed(SlashCommandInteractionEvent event, QuizQuestionDifficultyLevel difficultyLevel, QuizQuestion randomQuestion) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Quiz question level: " + difficultyLevel.getDisplayName())
                .setColor(difficultyLevel.getColor());

        embedBuilder.addField("Question", randomQuestion.getQuestion(), false);

        StringBuilder answers = new StringBuilder();
        randomQuestion.getAnswers().forEach(answer ->
                answers.append(answer.getAnswer()).append("\n")
        );


        embedBuilder.addField("Answers", answers.toString(), true);

        embedBuilder.setFooter(Calendar.getInstance().getTime().toString());

        List<ItemComponent> actionRow = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(1);

        randomQuestion.getAnswers().forEach(quizQuestionAnswer -> {
            actionRow.add(Button.success("quizanswer%10%" + randomQuestion.getId() + "%10%" + quizQuestionAnswer.getAnswer().replace(" ", "%20%"), convertNumberToWord(counter.get())));
            counter.getAndIncrement();
        });

        if (actionRow.isEmpty()) {
            actionRow.add(Button.danger("quiz-error-noanswer", "No Answer available").withEmoji(Emoji.fromUnicode("U+1F928")).asDisabled());
            actionRow.add(Button.success("quizadd%10%answer%10%"+randomQuestion.getId(), "But you can add one"));
        }

        event.replyEmbeds(embedBuilder.build()).addActionRow(actionRow).queue();
    }

    private String convertNumberToWord(final int number) {
        String[] digitEmojis = {"0️⃣", "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣"};

        String numberStr = String.valueOf(number);
        StringBuilder emojiStr = new StringBuilder();

        for (char digit : numberStr.toCharArray()) {
            emojiStr.append(digitEmojis[digit - '0']);
        }

        return emojiStr.toString();
    }
}
