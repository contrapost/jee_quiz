package me.contrapost.gameApi.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by alexandershipunov on 21/11/2016.
 *
 */
@ApiModel("An answer check")
public class AnswerCheckDTO {

    @ApiModelProperty("The boolean result reflects if the answer was correct")
    public String isCorrect;

    @SuppressWarnings("unused")
    public AnswerCheckDTO() {
    }

    public AnswerCheckDTO(String isCorrect) {
        this.isCorrect = isCorrect;
    }
}
