package de.felixschick.quizy.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class QuizQuestionAnswer {
    private String answer;
    private boolean correct;
}
