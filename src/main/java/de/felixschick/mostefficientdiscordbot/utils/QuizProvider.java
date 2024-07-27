package de.felixschick.mostefficientdiscordbot.utils;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.felixschick.mostefficientdiscordbot.MostEfficientDiscordBot;
import de.felixschick.mostefficientdiscordbot.enums.QuizQuestionDifficultyLevel;
import de.felixschick.mostefficientdiscordbot.objects.QuizQuestion;
import de.felixschick.mostefficientdiscordbot.objects.QuizQuestionAnswer;
import de.felixschick.mostefficientdiscordbot.sql.MySQL;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class QuizProvider {
    private MySQL mySQL;

    @Getter
    public AsyncLoadingCache<Integer, QuizQuestion> quizQuestionCache;

    private final ExecutorService executor;

    @Getter
    List<QuizQuestion> questions;

    public QuizProvider() {
        this.executor = MostEfficientDiscordBot.getExecutorService();

        mySQL = MostEfficientDiscordBot.getMySQL();
        
        quizQuestionCache = Caffeine.newBuilder()
                        .executor(executor)
                        .buildAsync(loadQuestions());
    }


    public QuizQuestion getQuizQuestion(int id) {
        QuizQuestion quizQuestion;

        try {
            quizQuestion = quizQuestionCache.get(id).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return quizQuestion;
    }

    public List<QuizQuestion> getAllQuizQuestions() {
        List<QuizQuestion> quizQuestions = new ArrayList<>();


        List<Integer> integers = callEveryQuestion().get();

        integers.forEach(integer -> {
            quizQuestions.add(getQuizQuestion(integer));
        });

        System.out.println(quizQuestions);
        return quizQuestions;
    }

    public List<QuizQuestion> getQuizQuestions(QuizQuestionDifficultyLevel difficultyLevel) {
        List<QuizQuestion> quizQuestions = new ArrayList<>();


        List<Integer> integers = callEveryQuestionByDifficultyLevel(difficultyLevel).get();

        integers.forEach(integer -> {
            quizQuestions.add(getQuizQuestion(integer));
        });

        System.out.println(quizQuestions);
        return quizQuestions;
    }

    public Optional<QuizQuestion> creatQuizQuestion(final String question, final QuizQuestionDifficultyLevel difficultyLevel, final List<QuizQuestionAnswer> answers) {
        final boolean isMultipleChoice =
                answers.stream().filter(quizQuestionAnswer -> quizQuestionAnswer.isCorrect() == true).toList().size() > 0;

        return saveQuizQuestion(new QuizQuestion(-1, question, isMultipleChoice, difficultyLevel, answers));
    }

    private Optional<List<Integer>> callEveryQuestionByDifficultyLevel(QuizQuestionDifficultyLevel difficultyLevel) {
        try {
            return (Optional<List<Integer>>) CompletableFuture.supplyAsync(() -> {
                         ResultSet resultSet = mySQL.qry("SELECT `id` FROM `quiz_questions` WHERE `difficultylevel` = '" + difficultyLevel + "'");

                         List<Integer> ids = new ArrayList<>();

                         try {
                             while (resultSet.next()) {
                                ids.add(resultSet.getInt("id"));
                             }
                         } catch (SQLException e) {
                             e.printStackTrace();
                         }
                         return Optional.of(ids);
                    }).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<List<Integer>> callEveryQuestion() {
        try {
            return (Optional<List<Integer>>) CompletableFuture.supplyAsync(() -> {
                ResultSet resultSet = mySQL.qry("SELECT `id` FROM `quiz_questions`");

                List<Integer> ids = new ArrayList<>();

                try {
                    while (resultSet.next()) {
                        ids.add(resultSet.getInt("id"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return Optional.of(ids);
            }).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Boolean> existsInSQL(final int id) {
        try {
            return (Optional<Boolean>) CompletableFuture.supplyAsync(() -> {
                final ResultSet resultSet = mySQL.qry("SELECT * FROM `quiz_questions` WHERE `id` = " + id);
                try {
                    if (resultSet.next()) {
                        return Optional.of(true);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return Optional.of(false);
            }).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<QuizQuestion> saveQuizQuestion(QuizQuestion quizQuestion) {
        StringBuilder answerBuilder = new StringBuilder();


        quizQuestion.getAnswers().forEach(quizQuestionAnswer -> {
            answerBuilder.append(quizQuestionAnswer.getAnswer() + "," + quizQuestionAnswer.isCorrect() + ";");
        });

        AtomicReference<Optional<QuizQuestion>> optionalQuizQuestion = new AtomicReference<>(Optional.empty());

        CompletableFuture.supplyAsync(() ->
                    mySQL.update("INSERT INTO `quiz_questions`(`question`, `difficultylevel`, `answers`) VALUES ('"+quizQuestion.getQuestion()+"', '"+quizQuestion.getDifficultyLevel()+"', '"+answerBuilder.toString()+"');")
                ).thenAccept(resultSet -> {
                    if (resultSet != -1) {
                        CompletableFuture<QuizQuestion> completableFuture = new CompletableFuture<>();
                        runAsync(() -> {
                            Optional<Integer> id = getQuizQuestionIDByQuestion(quizQuestion.getQuestion());
                            if (id.isPresent()) {
                                quizQuestion.setId(id.get());
                                completableFuture.complete(quizQuestion);
                                quizQuestionCache.put(quizQuestion.getId(), completableFuture);
                                optionalQuizQuestion.set(Optional.of(quizQuestion));
                            } else
                                throw new RuntimeException("Saving failed ID missing");
                        });
                    } else
                        throw new RuntimeException("Saving failed");
        });

        return optionalQuizQuestion.get();
    }

    private Optional<Integer> getQuizQuestionIDByQuestion(String question) {
        try {
            return (Optional<Integer>) CompletableFuture.supplyAsync(() -> {
                ResultSet resultSet = mySQL.qry("SELECT * FROM `quiz_questions` WHERE `question` = '" + question + "'");
                try {
                    if (resultSet.next())
                        return Optional.of(resultSet.getInt("id"));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return Optional.empty();
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private void runAsync(Runnable task) {
        executor.execute(task);
    }
    
    private AsyncCacheLoader<Integer, QuizQuestion> loadQuestions() {
        return (id, executor) -> {
            CompletableFuture<QuizQuestion> completableFuture = new CompletableFuture<>();

            runAsync(() -> {
                getQuizQuestionByIDFromSQL(id).ifPresent(quizQuestion -> {
                    completableFuture.complete(quizQuestion);
                });
            });
            return completableFuture;
        };
    }

    private Optional<QuizQuestion> getQuizQuestionByIDFromSQL(final int id) {
        try {
            return (Optional<QuizQuestion>) CompletableFuture.supplyAsync(() -> {
                final ResultSet resultSet = mySQL.qry("SELECT * FROM `quiz_questions` WHERE `id` = " + id);
                try {
                    if (resultSet.next()) {
                        final String question = resultSet.getString("question");
                        final QuizQuestionDifficultyLevel difficultyLevel = QuizQuestionDifficultyLevel.
                                valueOf(resultSet.getString("difficultylevel"));
                        final List<QuizQuestionAnswer> answers = new ArrayList<>();

                        final String[] rawAnswers = resultSet.getString("answers").split(";");

                        for (String rawAnswer : rawAnswers) {
                            String[] splitAnswer = rawAnswer.split(",");
                            if(splitAnswer.length == 2) {
                                String answer = splitAnswer[0];
                                Boolean correct = Boolean.valueOf(splitAnswer[1]);

                                answers.add(new QuizQuestionAnswer(answer, correct));
                            }
                        }



                        final boolean isMultipleChoice = (!answers.isEmpty() && !answers.stream().filter(QuizQuestionAnswer::isCorrect).toList().isEmpty());

                        QuizQuestion quizQuestion = new QuizQuestion(id, question, isMultipleChoice, difficultyLevel, answers);
                        return Optional.of(quizQuestion);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return Optional.empty();
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void saveAllQuizQuestionsToSQL() {
        for (final int id : quizQuestionCache.asMap().keySet()) {
            System.out.println("test " + id);
            QuizQuestion quizQuestion = getQuizQuestion(id);

            StringBuilder answerBuilder = new StringBuilder();

            quizQuestion.getAnswers().forEach(quizQuestionAnswer -> {
                answerBuilder.append(quizQuestionAnswer.getAnswer() + "," + quizQuestionAnswer.isCorrect() + ";");
            });

            System.out.println(quizQuestion.getId());


            mySQL.update("UPDATE `quiz_questions` SET `question` = '" + quizQuestion.getQuestion() + "', `difficultylevel` = '" + quizQuestion.getDifficultyLevel() + "', `answers` = '" + answerBuilder.toString() + "' WHERE `id` = '" + id + "';");
            System.out.println("Quiz Question " + id + " saved");
        }
    }
}
