package de.felixschick.quizy.utils;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.felixschick.quizy.enums.QuizQuestionDifficultyLevel;
import de.felixschick.quizy.objects.QuizQuestion;
import de.felixschick.quizy.objects.QuizQuestionAnswer;
import de.felixschick.quizy.sql.MySQL;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class QuizProvider {

    private final MySQL mySQL;
    private final ExecutorService executor;

    @Getter
    private AsyncLoadingCache<Integer, QuizQuestion> quizQuestionCache;

    @Getter
    private List<QuizQuestion> questions;

    @Autowired
    public QuizProvider(MySQL mySQL, ExecutorService executor) {
        this.mySQL = mySQL;
        this.executor = executor;
    }

    @PostConstruct
    public void init() {
        quizQuestionCache = Caffeine.newBuilder()
                .executor(executor)
                .buildAsync(loadQuestionById());
    }

    @PreDestroy
    public void preDestroy() {
        saveAllQuizQuestionsToSQL();
    }

    public QuizQuestion getQuizQuestion(int id) {
        try {
            return quizQuestionCache.get(id).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error fetching quiz question with id {}: {}", id, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<QuizQuestion> getAllQuizQuestions() {
        return callEveryQuestion().map(ids -> {
            List<QuizQuestion> quizQuestions = new ArrayList<>();
            ids.forEach(id -> quizQuestions.add(getQuizQuestion(id)));
            return quizQuestions;
        }).orElseGet(ArrayList::new);
    }

    public List<QuizQuestion> getQuizQuestions(QuizQuestionDifficultyLevel difficultyLevel) {
        return callEveryQuestionByDifficultyLevel(difficultyLevel).map(ids -> {
            List<QuizQuestion> quizQuestions = new ArrayList<>();
            ids.forEach(id -> quizQuestions.add(getQuizQuestion(id)));
            return quizQuestions;
        }).orElseGet(ArrayList::new);
    }

    public List<QuizQuestion> getQuizQuestions(final long guildID, QuizQuestionDifficultyLevel difficultyLevel) {
        return callEveryQuestionByDifficultyLevel(difficultyLevel).map(ids -> {
            List<QuizQuestion> quizQuestions = new ArrayList<>();
            ids.forEach(id -> {
                QuizQuestion quizQuestion = getQuizQuestion(id);
                if (quizQuestion.getGuildID() == guildID || quizQuestion.getGuildID() == -1) {
                    quizQuestions.add(quizQuestion);
                }
            });
            return quizQuestions;
        }).orElseGet(ArrayList::new);
    }

    public Optional<QuizQuestion> createQuizQuestion(final long guildID, final String question, final QuizQuestionDifficultyLevel difficultyLevel, final List<QuizQuestionAnswer> answers) {
        return saveQuizQuestion(new QuizQuestion(-1, question, guildID, difficultyLevel, answers));
    }

    private Optional<List<Integer>> callEveryQuestionByDifficultyLevel(QuizQuestionDifficultyLevel difficultyLevel) {
        return runAsync(() -> {
            List<Integer> ids = new ArrayList<>();
            try (ResultSet resultSet = mySQL.query("SELECT id FROM quiz_questions WHERE difficultylevel = '" + difficultyLevel + "'")) {
                while (resultSet.next()) {
                    ids.add(resultSet.getInt("id"));
                }
            } catch (SQLException e) {
                log.error("Error fetching questions by difficulty level {}: {}", difficultyLevel, e.getMessage());
            }
            return ids;
        });
    }

    private Optional<List<Integer>> callEveryQuestion() {
        return runAsync(() -> {
            List<Integer> ids = new ArrayList<>();
            try (ResultSet resultSet = mySQL.query("SELECT id FROM quiz_questions")) {
                while (resultSet.next()) {
                    ids.add(resultSet.getInt("id"));
                }
            } catch (SQLException e) {
                log.error("Error fetching all questions: {}", e.getMessage());
            }
            return ids;
        });
    }

    private Optional<Boolean> existsInSQL(final int id) {
        return runAsync(() -> {
            try (ResultSet resultSet = mySQL.query("SELECT id FROM quiz_questions WHERE id = " + id)) {
                return resultSet.next();
            } catch (SQLException e) {
                log.error("Error checking if question exists with id {}: {}", id, e.getMessage());
                return false;
            }
        });
    }

    private Optional<QuizQuestion> saveQuizQuestion(QuizQuestion quizQuestion) {
        StringBuilder answerBuilder = new StringBuilder();
        quizQuestion.getAnswers().forEach(answer -> answerBuilder.append(answer.getAnswer()).append(",").append(answer.isCorrect()).append(";"));

        AtomicReference<Optional<QuizQuestion>> optionalQuizQuestion = new AtomicReference<>(Optional.empty());
        runAsync(() -> {
            int result = mySQL.update("INSERT INTO quiz_questions (question, guildid, difficultylevel, answers) VALUES ('"
                    + quizQuestion.getQuestion() + "', " + quizQuestion.getGuildID() + ", '" + quizQuestion.getDifficultyLevel() + "', '"
                    + answerBuilder.toString() + "')");
            if (result != -1) {
                getQuizQuestionIDByQuestion(quizQuestion.getQuestion()).ifPresent(id -> {
                    quizQuestion.setId(id);
                    quizQuestionCache.put(id, CompletableFuture.completedFuture(quizQuestion));
                    optionalQuizQuestion.set(Optional.of(quizQuestion));
                });
            } else {
                log.error("Saving quiz question failed.");
                throw new RuntimeException("Saving quiz question failed.");
            }
        });

        return optionalQuizQuestion.get();
    }

    private Optional<Integer> getQuizQuestionIDByQuestion(String question) {
        return runAsync(() -> {
            try (ResultSet resultSet = mySQL.query("SELECT id FROM quiz_questions WHERE question = '" + question + "'")) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            } catch (SQLException e) {
                log.error("Error fetching question ID by question {}: {}", question, e.getMessage());
            }
            return null;
        });
    }

    private AsyncCacheLoader<Integer, QuizQuestion> loadQuestionById() {
        return (id, executor) -> {
            CompletableFuture<QuizQuestion> future = new CompletableFuture<>();
            runAsync(() -> {
                getQuizQuestionByIDFromSQL(id).ifPresent(future::complete);
            });
            return future;
        };
    }

    private Optional<QuizQuestion> getQuizQuestionByIDFromSQL(final int id) {
        return runAsync(() -> {
            try (ResultSet resultSet = mySQL.query("SELECT * FROM quiz_questions WHERE id = " + id)) {
                if (resultSet.next()) {
                    String question = resultSet.getString("question");
                    QuizQuestionDifficultyLevel difficultyLevel = QuizQuestionDifficultyLevel.valueOf(resultSet.getString("difficultylevel"));
                    List<QuizQuestionAnswer> answers = new ArrayList<>();
                    long guildID = resultSet.getLong("guildid");

                    String[] rawAnswers = resultSet.getString("answers").split(";");
                    for (String rawAnswer : rawAnswers) {
                        String[] splitAnswer = rawAnswer.split(",");
                        if (splitAnswer.length == 2) {
                            String answer = splitAnswer[0];
                            boolean correct = Boolean.parseBoolean(splitAnswer[1]);
                            answers.add(new QuizQuestionAnswer(answer, correct));
                        }
                    }

                    return new QuizQuestion(id, question, guildID, difficultyLevel, answers);
                }
            } catch (SQLException e) {
                log.error("Error fetching quiz question by ID {}: {}", id, e.getMessage());
            }
            return null;
        });
    }

    private void runAsync(Runnable task) {
        executor.execute(task);
    }

    private <T> Optional<T> runAsync(SupplierWithSQLException<T> supplier) {
        try {
            return Optional.ofNullable(CompletableFuture.supplyAsync(() -> {
                try {
                    return supplier.get();
                } catch (SQLException e) {
                    log.error("Error executing async task: {}", e.getMessage());
                    throw new RuntimeException(e);
                }
            }, executor).get());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error waiting for async task: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @FunctionalInterface
    private interface SupplierWithSQLException<T> {
        T get() throws SQLException;
    }

    public void saveAllQuizQuestionsToSQL() {
        for (int id : quizQuestionCache.asMap().keySet()) {
            QuizQuestion quizQuestion = getQuizQuestion(id);
            StringBuilder answerBuilder = new StringBuilder();
            quizQuestion.getAnswers().forEach(answer -> answerBuilder.append(answer.getAnswer()).append(",").append(answer.isCorrect()).append(";"));
            mySQL.update("UPDATE quiz_questions SET question = '" + quizQuestion.getQuestion() + "', difficultylevel = '" + quizQuestion.getDifficultyLevel() + "', guildid = " + quizQuestion.getGuildID() + ", answers = '" + answerBuilder.toString() + "' WHERE id = " + id);
            log.info("Quiz Question {} saved", id);
        }
    }
}
