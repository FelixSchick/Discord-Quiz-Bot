package de.felixschick.quizy.objects;

import de.felixschick.quizy.enums.QuizQuestionDifficultyLevel;
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
    private long guildID;
    private QuizQuestionDifficultyLevel difficultyLevel;
    private List<QuizQuestionAnswer> answers;

    @Override
    public String toString() {
        return "QuizQuestion{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", guildID=" + guildID +
                ", difficultyLevel=" + difficultyLevel +
                ", answers=" + answers +
                '}';
    }
}
