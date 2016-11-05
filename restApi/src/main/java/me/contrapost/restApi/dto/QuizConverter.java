package me.contrapost.restApi.dto;

import me.contrapost.jee_quiz.entity.Quiz;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by alexandershipunov on 30/10/2016.
 * Quiz converter
 */
public class QuizConverter {

    private QuizConverter() {}

    public static QuizDTO transform(Quiz quiz) {
        Objects.requireNonNull(quiz);

        QuizDTO dto = new QuizDTO();
        dto.id = String.valueOf(quiz.getId());
        dto.question = quiz.getQuestion();
        dto.specifyingCategoryId = String.valueOf(quiz.getSpecifyingCategory().getId());
        dto.answerMap = quiz.getAnswerMap();

        return dto;
    }

    public static List<QuizDTO> transform(List<Quiz> quizes){
        Objects.requireNonNull(quizes);

        return quizes.stream()
                .map(QuizConverter::transform)
                .collect(Collectors.toList());
    }
}
