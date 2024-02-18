package de.felixschick.mostefficientdiscordbot.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class QuizQuestionAnswer {
    private String answer;
    private boolean correct;
}
