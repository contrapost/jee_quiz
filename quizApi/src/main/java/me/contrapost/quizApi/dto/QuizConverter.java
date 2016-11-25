package me.contrapost.quizApi.dto;

import me.contrapost.jee_quiz.entity.Quiz;
import me.contrapost.quizApi.dto.collection.ListDTO;

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

    public static ListDTO<QuizDTO> transform(List<Quiz> quizzes, int offset,
                                                              int limit){

        List<QuizDTO> dtoList = null;
        if(quizzes != null){
            dtoList = quizzes.stream()
                    .skip(offset) // this is a good example of how streams simplify coding
                    .limit(limit)
                    .map(QuizConverter::transform)
                    .collect(Collectors.toList());
        }

        ListDTO<QuizDTO> dto = new ListDTO<>();
        dto.list = dtoList;
        dto._links = new ListDTO.ListLinks();
        dto.rangeMin = offset;
        dto.rangeMax = dto.rangeMin + dtoList.size() - 1;
        dto.totalSize = quizzes.size();

        return dto;
    }
}
