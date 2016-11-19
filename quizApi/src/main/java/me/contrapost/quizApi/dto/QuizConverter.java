package me.contrapost.quizApi.dto;

import me.contrapost.jee_quiz.entity.Quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by alexandershipunov on 19/11/2016.
 *
 */
public class QuizConverter {
    private QuizConverter() {}

    public static QuizDTO transform(Quiz quiz) {
        Objects.requireNonNull(quiz);

        QuizDTO dto = new QuizDTO();
        dto.id = String.valueOf(quiz.getId());
        dto.question = quiz.getQuestion();
        dto.specifyingCategoryId = String.valueOf(quiz.getSpecifyingCategory().getId());
        dto.answerList = new ArrayList<>(quiz.getAnswerMap().keySet());

        return dto;
    }

    public static List<QuizDTO> transform(List<Quiz> quizzes){
        Objects.requireNonNull(quizzes);

        return quizzes.stream()
                .map(QuizConverter::transform)
                .collect(Collectors.toList());
    }
}