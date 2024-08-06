package de.felixschick.quizy.controllers;

import de.felixschick.quizy.QuizyApplication;
import de.felixschick.quizy.objects.QuizQuestion;
import de.felixschick.quizy.utils.QuizProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private QuizProvider quizProvider;

    @GetMapping("/questions")
    public List<QuizQuestion> getAllQuestions() {
        return quizProvider.getAllQuizQuestions();
    }

}
