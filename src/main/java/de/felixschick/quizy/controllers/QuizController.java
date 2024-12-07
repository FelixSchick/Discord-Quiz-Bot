package de.felixschick.quizy.controllers;

import de.felixschick.quizy.DTO.QuestionRequest;
import de.felixschick.quizy.QuizyApplication;
import de.felixschick.quizy.objects.QuizQuestion;
import de.felixschick.quizy.utils.QuizProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private QuizProvider quizProvider;

    @GetMapping("/questions")
    public List<QuizQuestion> getAllQuestions() {
        return quizProvider.getAllQuizQuestions();
    }

    @GetMapping("/question")
    public QuizQuestion getQuestion(@RequestParam(name = "id") int id) {
        return quizProvider.getQuizQuestion(id);
    }

    @PostMapping("/question")
    public String addQuestion(@RequestBody QuestionRequest questionRequest) {
        try {
            quizProvider.createQuizQuestion(
                    questionRequest.getGuildID(),
                    questionRequest.getQuestion(),
                    questionRequest.getDifficultyLevel(),
                    questionRequest.getAnswers()
            );

            return "success";
        } catch (Exception exception) {
            return "failed";
        }
    }

    @PatchMapping("/question/{id}")
    public String updateQuestion(@PathVariable int id, @RequestBody QuestionRequest questionRequest) {
        try {
            quizProvider.updateQuestion(id, questionRequest);
            return "success";
        } catch (Exception exception) {
            return "failed";
        }
    }

}
