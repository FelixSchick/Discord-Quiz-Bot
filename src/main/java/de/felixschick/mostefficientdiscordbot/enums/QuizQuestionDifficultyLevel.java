package de.felixschick.mostefficientdiscordbot.enums;

import lombok.Getter;

public enum QuizQuestionDifficultyLevel {
    EASY("Easy"),
    MEDIUM("medium"),
    HARD("hard");

    @Getter
    private String displayName;

    QuizQuestionDifficultyLevel(String displayName) {
        this.displayName = displayName;
    }
}
