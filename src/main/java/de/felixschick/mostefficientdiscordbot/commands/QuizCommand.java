package de.felixschick.mostefficientdiscordbot.commands;

import de.felixschick.mostefficientdiscordbot.MostEfficientDiscordBot;
import de.felixschick.mostefficientdiscordbot.commands.interfaces.SlashCommand;
import de.felixschick.mostefficientdiscordbot.enums.QuizQuestionDifficultyLevel;
import de.felixschick.mostefficientdiscordbot.handler.QuestionCreationHandler;
import de.felixschick.mostefficientdiscordbot.helper.MessageHelper;
import de.felixschick.mostefficientdiscordbot.objects.QuizQuestion;
import de.felixschick.mostefficientdiscordbot.objects.QuizQuestionAnswer;
import de.felixschick.mostefficientdiscordbot.utils.QuizProvider;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class QuizCommand implements SlashCommand {

    private QuizProvider quizProvider;

    private QuestionCreationHandler creationHandler;

    private Random random;

    public QuizCommand() {
        random = new Random();
        quizProvider = MostEfficientDiscordBot.getQuizProvider();
        creationHandler = MostEfficientDiscordBot.getCreationHandler();
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
                sendQuestionListEmbed(event.getChannel().asTextChannel());

            } else if (event.getSubcommandName().equalsIgnoreCase("question")) {
                final QuizQuestionDifficultyLevel difficultyLevel = QuizQuestionDifficultyLevel.valueOf(event.getOption("difficultylevel").getAsString());
                final List<QuizQuestion> quizQuestions = quizProvider.getQuizQuestions(difficultyLevel);
                final QuizQuestion randomQuestion = quizQuestions.get(random.nextInt(quizQuestions.size()));

                sendQuestionEmbed(event, difficultyLevel, randomQuestion);
            }
        }
    }



    private void sendQuestionListEmbed(TextChannel targetChannel) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Quiz questions")
                .setColor(Color.BLUE);

        for (QuizQuestion quizQuestion : quizProvider.getAllQuizQuestions()) {
            StringBuilder answerBuilder = new StringBuilder();
            quizQuestion.getAnswers().forEach(quizQuestionAnswer ->
                answerBuilder.append("  -").append(quizQuestionAnswer.getAnswer()).append("(").append(quizQuestionAnswer.isCorrect()).append(")\n")
            );
            embedBuilder.addField("Question " + quizQuestion.getId(), quizQuestion.getQuestion() + "\n" + answerBuilder.toString(), false);
        }

        embedBuilder.setFooter(Calendar.getInstance().getTime().toString());

        targetChannel.sendMessageEmbeds(embedBuilder.build()).queue();
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


        event.getChannel().sendMessageEmbeds(embedBuilder.build()).addActionRow(actionRow).queue();
    }

    private String convertNumberToWord(int number) {
        String[] digitEmojis = {"0️⃣", "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣"};

        boolean isNegative = number < 0;
        if (isNegative) {
            number = -number;
        }

        String numberStr = String.valueOf(number);
        StringBuilder emojiStr = new StringBuilder();

        if (isNegative) {
            emojiStr.append("➖");
        }

        for (char digit : numberStr.toCharArray()) {
            emojiStr.append(digitEmojis[digit - '0']);
        }

        return emojiStr.toString();
    }
}
