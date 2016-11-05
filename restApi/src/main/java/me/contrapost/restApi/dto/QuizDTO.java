package me.contrapost.restApi.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import me.contrapost.jee_quiz.entity.SpecifyingCategory;

import java.util.Map;

/**
 * Created by alexandershipunov on 30/10/2016.
 * QiuzDTO
 */
@ApiModel("A quiz")
public class QuizDTO {

    @ApiModelProperty("The id of the quiz")
    public String id;

    @ApiModelProperty("The quiz question")
    public String question;

    @ApiModelProperty("Id of the specifying category the quiz belongs to")
    public String specifyingCategoryId;

    @ApiModelProperty("The set of answers marked with false and true")
    public Map<String, Boolean> answerMap;

    public QuizDTO() {
    }

    public QuizDTO(String id, String question, String categoryId, Map<String, Boolean> answers) {
        this.id = id;
        this.question = question;
        specifyingCategoryId = categoryId;
        answerMap = answers;
    }
}
