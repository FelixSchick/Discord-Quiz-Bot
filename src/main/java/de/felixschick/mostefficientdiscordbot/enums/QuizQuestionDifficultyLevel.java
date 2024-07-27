package de.felixschick.mostefficientdiscordbot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

@Getter
@AllArgsConstructor
public enum QuizQuestionDifficultyLevel {
    EASY("easy", Color.GREEN),
    MEDIUM("medium", Color.YELLOW),
    HARD("hard", Color.RED);

    private final String displayName;

    private final Color color;
}
