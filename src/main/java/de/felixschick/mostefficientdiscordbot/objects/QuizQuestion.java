package de.felixschick.mostefficientdiscordbot.objects;

import de.felixschick.mostefficientdiscordbot.enums.QuizQuestionDifficultyLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class QuizQuestion {
    private int id;
    private String question;
    private boolean multipleChoice;
    private QuizQuestionDifficultyLevel difficultyLevel;
    private List<QuizQuestionAnswer> answers;

    @Override
    public String toString() {
        return "QuizQuestion{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", multipleChoice=" + multipleChoice +
                ", difficultyLevel=" + difficultyLevel +
                ", answers=" + answers +
                '}';
    }
}
