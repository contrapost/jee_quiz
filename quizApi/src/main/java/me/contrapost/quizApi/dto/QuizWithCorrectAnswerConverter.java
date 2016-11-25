package me.contrapost.quizApi.dto;

import me.contrapost.jee_quiz.entity.Quiz;
import me.contrapost.quizApi.dto.collection.ListDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by alexandershipunov on 30/10/2016.
 * Quiz converter
 */
public class QuizWithCorrectAnswerConverter {

    private QuizWithCorrectAnswerConverter() {}

    public static QuizWithCorrectAnswerDTO transform(Quiz quiz) {
        Objects.requireNonNull(quiz);

        QuizWithCorrectAnswerDTO dto = new QuizWithCorrectAnswerDTO();
        dto.id = String.valueOf(quiz.getId());
        dto.question = quiz.getQuestion();
        dto.specifyingCategoryId = String.valueOf(quiz.getSpecifyingCategory().getId());
        dto.answerMap = new HashMap<>(quiz.getAnswerMap());

        return dto;
    }
}
