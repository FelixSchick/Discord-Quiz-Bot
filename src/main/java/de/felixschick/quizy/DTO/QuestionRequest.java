package de.felixschick.quizy.DTO;

import de.felixschick.quizy.enums.QuizQuestionDifficultyLevel;
import de.felixschick.quizy.objects.QuizQuestionAnswer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class QuestionRequest {
    private String question;
    private long guildID;
    private QuizQuestionDifficultyLevel difficultyLevel;
    private List<QuizQuestionAnswer> answers;
}
