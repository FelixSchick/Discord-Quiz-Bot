package de.felixschick.quizy.controllers;

import de.felixschick.quizy.QuizyApplication;
import de.felixschick.quizy.objects.QuizQuestion;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @GetMapping("/questions")
    public List<QuizQuestion> getAllQuestions() {
        return QuizyApplication.getQuizProvider().getAllQuizQuestions();
    }

}
